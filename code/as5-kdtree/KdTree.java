import java.util.Comparator;
import java.util.Iterator;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;


public class KdTree {
    private Node root;
    private int size = 0;
    private Point2D nearestPoint;
    private double nearestDist = Double.POSITIVE_INFINITY;
    private boolean verbose = false;

    private class Node {
        private Point2D key;
        private int size;
        private Node left, right;
        public Node(Point2D key, int size) {
            this.key = key;
            this.size = size;
        }
    }


    public KdTree()  {
        // construct an empty set of points
    }

    public boolean isEmpty()  {
        // is the set empty?
        return size == 0;
    }

    public int size()  {
        // number of points in the set
        return size;
    }

    public void insert(Point2D p) {
        // add the point to the set (if it is not already in the set)
        root = insert(p, root, true);
    }

    private Node insert(Point2D p, Node current, boolean isVertical) {
        if (current == null) {
            size++;
            return new Node(p, size);
        } else if (current.key.equals(p)) {
            return current;
        }

        int cmp = comparePoints(p, current.key, isVertical);
        if (cmp < 0)      {current.left = insert(p, current.left, !isVertical);}
        else if (cmp > 0) {current.right = insert(p, current.right, !isVertical);}
        else if (p != current.key) {
            // on the same plane but is a new point
            current.right = insert(p, current.right, !isVertical);
        }
        return current;
    }

    public boolean contains(Point2D p) {
        // does the set contain point p?
        return contains(p, root, true);
    }

    private boolean contains(Point2D p, Node current, boolean isVertical) {
        if (current == null)            {return false;}
        else if (current.key.equals(p)) {return true;}

        int cmp = comparePoints(p, current.key, isVertical);
        if (cmp < 0)      {return contains(p, current.left, !isVertical);}
        else              {return contains(p, current.right, !isVertical);}
    }

    private int comparePoints(Point2D p, Point2D q, boolean isVertical) {
        Comparator<Point2D> comparator = Point2D.Y_ORDER;
        if (isVertical) {comparator = Point2D.X_ORDER;}

        return comparator.compare(p, q);
    }

    public void draw()  {
        // draw all points to standard draw
        if (root != null) {
            // draw(root, true, rect.xmin(), rect.xmax(), rect.ymin(), rect.ymax());
            draw(root, true, 0, 1, 0, 1);
        }
    }

    private void draw(Node current, boolean isVertical, double xmin, double xmax, double ymin, double ymax) {
        if (current == null) {return;}

        StdDraw.setPenRadius(0.002);
        StdDraw.setPenColor(StdDraw.BLACK);
        current.key.draw();
        StdDraw.textRight(current.key.x(), current.key.y(), "" + current.size);
        StdDraw.setPenRadius(0.005);

        if (isVertical) {
            StdDraw.setPenColor(StdDraw.BOOK_RED);
            StdDraw.line(current.key.x(), current.key.y(), current.key.x(), ymax);
            StdDraw.line(current.key.x(), current.key.y(), current.key.x(), ymin);
            draw(current.left, !isVertical, xmin, current.key.x(), ymin, ymax);
            draw(current.right, !isVertical, current.key.x(), xmax, ymin, ymax);
        } else {
            StdDraw.setPenColor(StdDraw.BOOK_BLUE);
            StdDraw.line(current.key.x(), current.key.y(), xmin, current.key.y());
            StdDraw.line(current.key.x(), current.key.y(), xmax, current.key.y());
            draw(current.left, !isVertical, xmin, xmax, ymin, current.key.y());
            draw(current.right, !isVertical, xmin, xmax, current.key.y(), ymax);
        }

        return;
    }

    public Iterable<Point2D> range(RectHV rect) {
        // all points that are inside the rectangle
        return new RangeIterable(rect);
    }

    private class RangeIterable implements Iterable<Point2D> {
        private RectHV rect;

        public RangeIterable(RectHV rect) {
            this.rect = rect;
        }

        public Iterator<Point2D> iterator() {
            return new RangeIterator();
        }

        private class RangeIterator implements Iterator<Point2D> {
            private ListItem currentItem;
            private ListItem nextItem;
            private ListItem rootItem;
            private RectHV subrect;

            private class ListItem {
                public Node node;
                public ListItem next;
                public ListItem(Node node) {
                    this.node = node;
                }
            }

            public RangeIterator() {
                rangeSearch(root, true, 0, 0, 1, 1);
                nextItem = rootItem;
            }


            private void appendListItem(Node current) {
                ListItem newItem = new ListItem(current);
                if (currentItem == null) {
                    // is first item
                    rootItem = newItem;
                    currentItem = rootItem;
                } else {
                    currentItem.next = newItem;
                    currentItem = currentItem.next;
                }
            }

            private void rangeSearch(Node current, boolean isVertical, double xmin, double ymin, double xmax, double ymax) {
                if (current == null) {return;}

                if (rect.contains(current.key)) {
                    appendListItem(current);
                }

                if (isVertical) {
                    // left
                    subrect = new RectHV(xmin, ymin, current.key.x(), ymax);
                    if (rect.intersects(subrect)) {
                        rangeSearch(current.left, !isVertical, xmin, ymin, current.key.x(), ymax);
                    }

                    // right
                    subrect = new RectHV(current.key.x(), ymin, xmax, ymax);
                    if (rect.intersects(subrect)) {
                        rangeSearch(current.right, !isVertical, current.key.x(), ymin, xmax, ymax);
                    }
                } else {
                    // below
                    subrect = new RectHV(xmin, ymin, xmax, current.key.y());
                    if (rect.intersects(subrect)) {
                        rangeSearch(current.left, !isVertical, xmin, ymin, xmax, current.key.y());
                    }

                    // above
                    subrect = new RectHV(xmin, current.key.y(), xmax, ymax);
                    if (rect.intersects(subrect)) {
                        rangeSearch(current.right, !isVertical, xmin, current.key.y(), xmax, ymax);
                    }
                }
            }

            public boolean hasNext() {
                return nextItem != null;
            }

            public void remove() {
                throw new java.lang.UnsupportedOperationException();
            }

            public Point2D next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                currentItem = nextItem;
                nextItem = currentItem.next;
                return currentItem.node.key;
            }
        }
    }


    public Point2D nearest(Point2D query) {
        // a nearest neighbor in the set to point p; null if the set is empty
        if (isEmpty()) {
            return null;
        }
        if (verbose) {
            System.out.println("\nFind nearest to " + query);
        }
        nearest(root, true, query, 0, 0, 1, 1);
        this.nearestDist = Double.POSITIVE_INFINITY;
        return this.nearestPoint;
    }

    private void nearest(Node current, boolean isVertical, Point2D query, double xmin, double ymin, double xmax, double ymax) {

        if (current == null) {
            return;
        }

        String message = "current " + current.size + " " + current.key;

        double currentDist = query.distanceTo(current.key);
        if (currentDist < this.nearestDist) {
            this.nearestDist = currentDist;
            this.nearestPoint = current.key;
            message += " is new best dist!";
        }
        if (verbose) {
            System.out.println(message);
        }

        if (isVertical) {
            Point2D closestPointInOtherRectangle = new Point2D(current.key.x(), query.y());

            // left
            if (query.x() < current.key.x()) {
                // update nearest from left side
                if (verbose) {
                    System.out.println("left");
                }
                nearest(current.left, !isVertical, query, xmin, ymin, current.key.x(), ymax);

                if (query.distanceTo(closestPointInOtherRectangle) < this.nearestDist) {
                    // check right subrect as well
                    if (verbose) {
                        System.out.println("also check right of " + current.size);
                    }
                    nearest(current.right, !isVertical, query, current.key.x(), ymin, xmax, ymax);
                } else {
                    if (verbose) {
                        System.out.println("didn't check right of " + current.size);
                    }
                }
            } else {
                    // update nearest from right side
                if (verbose) {
                    System.out.println("right");
                }
                nearest(current.right, !isVertical, query, current.key.x(), ymin, xmax, ymax);

                if (query.distanceTo(closestPointInOtherRectangle) < this.nearestDist) {
                        // check left subrect as well
                    if (verbose) {
                        System.out.println("also check left of " + current.size);
                    }
                    nearest(current.left, !isVertical, query, xmin, ymin, current.key.x(), ymax);
                } else {
                    if (verbose) {
                        System.out.println("didn't check left of " + current.size);
                    }
                }
            }

        } else {
            Point2D closestPointInOtherRectangle = new Point2D(query.x(), current.key.y());

            // below
            if (query.y() < current.key.y()) {
                // update nearest from below side
                if (verbose) {
                    System.out.println("below");
                }
                nearest(current.left, !isVertical, query, xmin, ymin, xmax, current.key.y());

                if (query.distanceTo(closestPointInOtherRectangle) < this.nearestDist) {
                    // check above subrect as well
                    if (verbose) {
                        System.out.println("also check above " + current.size);
                    }
                    nearest(current.right, !isVertical, query, xmin, current.key.y(), xmax, ymax);
                } else {
                    if (verbose) {
                        System.out.println("didn't check above " + current.size);
                    }
                }
            } else {
                    // update nearest from above side
                if (verbose) {
                    System.out.println("above");
                }
                nearest(current.right, !isVertical, query, xmin, current.key.y(), xmax, ymax);

                if (query.distanceTo(closestPointInOtherRectangle) < this.nearestDist) {
                        // check below subrect as well
                    if (verbose) {
                        System.out.println("also check below " + current.size);
                    }
                    nearest(current.left, !isVertical, query, xmin, ymin, xmax, current.key.y());
                } else {
                    if (verbose) {
                        System.out.println("didn't check above " + current.size);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        // unit testing of the methods (optional)
        RectHV rect = new RectHV(0.13, 0.05, 0.96, 0.85);
        // RectHV rect = new RectHV(0.0, 0.0, 1.0, 1.0);
        StdDraw.show(0);
        KdTree kdtree = new KdTree();
        while (true) {
            if (StdDraw.mousePressed()) {
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();
                StdOut.printf("%8.6f %8.6f\n", x, y);
                Point2D p = new Point2D(x, y);
                if (rect.contains(p)) {
                    StdOut.printf("%8.6f %8.6f\n", x, y);
                    kdtree.insert(p);
                    StdDraw.clear();
                    rect.draw();
                    kdtree.draw();
                }
            }
            StdDraw.show(50);
        }
    }

}
