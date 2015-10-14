import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdDraw;


public class BruteCollinearPoints {

    private LineSegment[] segments;
    private int size;

    /**
     *
     * Brute force. Write a program BruteCollinearPoints.java that examines 4 points
     * at a time and checks whether they all lie on the same line segment, returning
     * all such line segments. To check whether the 4 points p, q, r, and s are
     * collinear, check whether the three slopes between p and q, between p and r, and
     * between p and s are all equal.
     *
     */
    public BruteCollinearPoints(Point[] points) {
        // finds all line segments containing 4 points

        if (points == null) {
            throw new java.lang.NullPointerException();
        }

        Point smallest;
        Point largest;
        segments = new LineSegment[1];
        size = 0;

        for (int i = 0; i < points.length; i++) {
            Point p = points[i];
            if (p == null) throw new java.lang.NullPointerException();

            outerloop:
            for (int j = i + 1; j < points.length; j++) {
                Point q = points[j];
                if (q == null) throw new java.lang.NullPointerException();

                double p_to_q = p.slopeTo(q);
                if (p_to_q == Double.NEGATIVE_INFINITY) throw new java.lang.IllegalArgumentException();

                for (int k = j + 1; k < points.length; k++) {
                    Point r = points[k];
                    if (r == null) throw new java.lang.NullPointerException();

                    double p_to_r = p.slopeTo(r);

                    // don't bother checking s, already not collinear
                    if (p_to_q != p_to_r) continue;

                    for (int l = 0; l < points.length; l++) {
                        Point s = points[l];
                        if (s == null) throw new java.lang.NullPointerException();

                        double p_to_s = p.slopeTo(s);

                        if (p_to_s == p_to_q) {
                            // Find extremes
                            smallest = p;
                            largest = p;
                            Point[] other_points = {q, r, s};
                            for (int m = 0; m < other_points.length; m++) {
                                Point point = other_points[m];
                                if (point.compareTo(largest) == 1) {
                                    largest = point;
                                } else if (point.compareTo(smallest) == -1) {
                                    smallest = point;
                                }
                            }

                            // create line segment
                            LineSegment ls = new LineSegment(smallest, largest);

                            // resize array
                            if (size == segments.length) {
                                LineSegment[] cp = new LineSegment[segments.length * 2];
                                for (int m = 0; m < segments.length; m++) {
                                    cp[m] = segments[m];
                                }
                                segments = cp;
                            }

                            // add line segment to segments
                            segments[size++] = ls;

                            // break and increment p
                            break outerloop;
                        }
                    }


                }

            }

        }

    }

    public int numberOfSegments() {
       // the number of line segments
        return size;
    }

    /**
     *
     * The method segments() should include each line segment containing 4 points
     * exactly once. If 4 points appear on a line segment in the order p→q→r→s,
     * then you should include either the line segment p→s or s→p (but not both)
     * and you should not include subsegments such as p→r or q→r. For simplicity,
     * we will not supply any input to BruteCollinearPoints that has 5 or more
     * collinear points.
     *
     */
    public LineSegment[] segments() {
        // the line segments
        LineSegment[] cp = new LineSegment[segments.length * 2];
        for (int m = 0; m < segments.length; m++) {
            cp[m] = segments[m];
        }
        return cp;
    }

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {
        // read the N points from a file
        In in = new In(args[0]);
        int N = in.readInt();
        Point[] points = new Point[N];
        for (int i = 0; i < N; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.show(0);
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
    }
}
