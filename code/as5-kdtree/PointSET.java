import java.util.Iterator;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;


public class PointSET {
	private SET<Point2D> setPoints;

	public PointSET()  {
		// construct an empty set of points
		setPoints = new SET<Point2D>();

	}

	public boolean isEmpty()  {
		// is the set empty?
		return setPoints.isEmpty();
	}

	public int size()  {
		// number of points in the set
		return setPoints.size();
	}

	public void insert(Point2D p) {
		// add the point to the set (if it is not already in the set)
		setPoints.add(p);
	}

	public boolean contains(Point2D p) {
		// does the set contain point p?
		return setPoints.contains(p);
	}

	public void draw()  {
		// draw all points to standard draw
		for (Point2D p : setPoints) {
			p.draw();
		}
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
			private Iterator<Point2D> iterator = setPoints.iterator();
			private Point2D current;
			private Point2D next = nextPoint();

			private Point2D nextPoint () {
				while (iterator.hasNext()) {
					next = iterator.next();
					if (rect.contains(next)) {
						return next;
					}
				}
				return null;
			}

			public boolean hasNext() {
				return next != null;
			}

			public void remove() {
				throw new java.lang.UnsupportedOperationException();
			}

			public Point2D next() {
				if (!hasNext()) {
					throw new java.util.NoSuchElementException();
				}
				current = next;
				next = nextPoint();
				return current;
			}
		}

	}


	public Point2D nearest(Point2D p) {
		// a nearest neighbor in the set to point p; null if the set is empty
		Point2D nearest = new Point2D(-1,-1);
		double nearest_dist = Double.POSITIVE_INFINITY;
		boolean nearest_has_been_set = false;

		for (Point2D q : setPoints) {
			double dist = p.distanceTo(q);
			if (dist != 0) {
				if (!nearest_has_been_set) {
					nearest = q;
					nearest_dist = dist;
					nearest_has_been_set = true;
				} else {
					if (dist < nearest_dist) {
						nearest = q;
						nearest_dist = dist;
					}
				}
			}
		}
		return nearest;
	}

	public static void main(String[] args) {
		// unit testing of the methods (optional)
		// RectHV rect = new RectHV(0.2, 0.1, 0.6, 0.3);
		RectHV rect = new RectHV(0.0, 0.0, 1.0, 1.0);
		StdDraw.show(0);
		PointSET kdtree = new PointSET();
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
