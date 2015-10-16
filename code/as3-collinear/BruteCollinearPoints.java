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
                    if (p_to_r == Double.NEGATIVE_INFINITY) throw new java.lang.IllegalArgumentException();

                    // don't bother checking s, already not collinear
                    if (p_to_q != p_to_r) continue;

                    for (int l = k + 1; l < points.length; l++) {
                        Point s = points[l];
                        if (s == null) throw new java.lang.NullPointerException();

                        double p_to_s = p.slopeTo(s);
                        if (p_to_s == Double.NEGATIVE_INFINITY) throw new java.lang.IllegalArgumentException();

                        if (p_to_s == p_to_q) {

                            System.out.println("Slope " + p_to_q + ": " + p + "-" + q + "-" + r + "-" + s);

                            Point[] segment_points = {p, q, r, s};
                            addLineSegment(segment_points);

                            // break and increment p
                            break outerloop;
                        }
                    }


                }

            }

        }

    }

    private void addLineSegment (Point[] segment_points) {
        Point[] extremes = findExtremes(segment_points);

        // create line segment
        LineSegment ls = new LineSegment(extremes[0], extremes[1]);

        resizeArray();

        // add line segment to segments
        segments[size++] = ls;
    }

    private void resizeArray () {
        // resize array
        if (size == segments.length) {
            LineSegment[] cp = new LineSegment[segments.length * 2];
            for (int m = 0; m < segments.length; m++) {
                cp[m] = segments[m];
            }
            segments = cp;
        }
    }


    private Point[] findExtremes(Point[] segment_points) {
        // Find extremes
        Point smallest = segment_points[0];
        Point largest = segment_points[0];
        for (int m = 1; m < segment_points.length; m++) {
            Point point = segment_points[m];
            if (point.compareTo(largest) == 1) {
                largest = point;
            } else if (point.compareTo(smallest) == -1) {
                smallest = point;
            }
        }

        Point[] extremes = {smallest, largest};
        return extremes;
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
        LineSegment[] cp = new LineSegment[size];
        for (int i = 0; i < size; i++) {
            cp[i] = segments[i];
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
            // System.out.println("Point: " + x + ", " + y);
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
