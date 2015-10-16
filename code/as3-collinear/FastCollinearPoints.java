import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdDraw;
import java.util.Arrays;

public class FastCollinearPoints {

    private LineSegment[] segments;
    private Point[] points;
    private Point[] aux;
    private int size;
    private boolean verbose = false;

    /**
     * A faster, sorting-based solution. Remarkably, it is possible to solve the
     * problem much faster than the brute-force solution described above. Given a
     * point p, the following method determines whether p participates in a set of 4
     * or more collinear points.
     *     - Think of p as the origin.
     *     - For each other point q, determine the slope it makes with p.
     *     - Sort the points according to the slopes they makes with p.
     *     - Check if any 3 (or more) adjacent points in the sorted order have equal
     *       slopes with respect to p. If so, these points, together with p, are
     *       collinear.
     * Applying this method for each of the N points in turn yields an
     * efficient algorithm to the problem. The algorithm solves the problem because
     * points that have equal slopes with respect to p are collinear, and sorting
     * brings such points together. The algorithm is fast because the bottleneck
     * operation is sorting.
     *
     * Performance requirement. The order of growth of the running time of your
     * program should be N^2 log N in the worst case and it should use space
     * proportional to N plus the number of line segments returned.
     * FastCollinearPoints should work properly even if the input has 5 or more
     * collinear points.
     *
     */
    public FastCollinearPoints(Point[] points) {
        // finds all line segments containing 4 points

        if (points == null) {
            throw new java.lang.NullPointerException();
        }

        this.points = points;
        segments = new LineSegment[1];
        size = 0;

        // copy to auxillary array for sorting
        this.aux = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            aux[i] = points[i];
        }


        for (int i = 0; i < points.length; i++) {
            // For each other point q, determine the slope it makes with p.
            // Sort the points according to the slopes they makes with p.
            // Check if any 3 (or more) adjacent points in the sorted order have equal
            // slopes with respect to p. If so, these points, together with p, are
            // collinear.
            // Applying this method for each of the N points

            Point p = points[i];
            if (verbose) System.out.println("\n-------------- Next p " + i + " " + p);
            if (p == null) throw new java.lang.NullPointerException();

            Arrays.sort(aux, p.slopeOrder());

            // init counter for first slope for first q
            // which is actually p
            double prev_slope = p.slopeTo(aux[0]);
            int collinear_count = 1;
            boolean inOrder = true;
            Point max_point = p;

            // all other q, other than those not seen
            for (int j = 1; j < aux.length; j++) {
                Point q = aux[j];
                double p_to_q = p.slopeTo(q);

                if (p_to_q == Double.NEGATIVE_INFINITY) throw new java.lang.IllegalArgumentException();

                else if (p_to_q == prev_slope) {
                    // same slope as adjacent slope
                    collinear_count++;

                    if (p.compareTo(q) > 0) {
                        // p is bigger than q
                        inOrder = false;
                    } else if (max_point.compareTo(q) < 0) {
                        // q is the biggest q in the segment
                        max_point = q;
                    }

                    if (verbose) System.out.println(collinear_count + " with slope " + p_to_q + " ordered " + inOrder + " q " + q);

                    // on last element check for line now
                    // because there will be no more new slopes
                    if (j == aux.length - 1) {
                        checkForLineSegment(inOrder, collinear_count, p, max_point);
                    }

                } else {
                    // new slope

                    // check if previous slope formed line segment
                    checkForLineSegment(inOrder, collinear_count, p, max_point);

                    // reset for new slope
                    prev_slope = p_to_q;
                    collinear_count = 1;
                    inOrder = p.compareTo(q) < 0;
                    max_point = q;

                    if (verbose) System.out.println(collinear_count + " with slope " + p_to_q + " ordered " + inOrder + " q " + q);
                }
            }
        }

    }

    private void checkForLineSegment (boolean inOrder, int collinear_count, Point min, Point max) {
        if (inOrder && collinear_count > 2) {
            // found line segment
            if (verbose) System.out.println("Found line segment " + min + " => " + max);

            // Point[] segment_points = getSegmentPoints(i, j, collinear_count);
            // Point[] extremes = findExtremes(segment_points);

            addLineSegment(min, max);
        }
    }


    /**
     *
     * @param  origin_idx       Index of origin point from which segment was found.
     * @param  last_idx         The index of the last point known to be part of this segment.
     * @param  collinear_count  Count of collinear points excluding origin.
     *
     */
    private Point[] getSegmentPoints(int origin_idx, int last_idx, int collinear_count) {
        Point[] segment_points = new Point[collinear_count + 1];

        // Put the origin at the beginning of the array of points
        segment_points[0] = this.points[origin_idx];

        // Goes from last point of segment to beginning
        for (int k = 0; k < collinear_count; k++) {
            segment_points[k+1] = this.aux[last_idx + (k - (collinear_count - 1))];
        }
        if (verbose) System.out.println(Arrays.toString(segment_points));

        return segment_points;

    }

    private void addLineSegment (Point smallest, Point largest) {

        // create line segment
        LineSegment ls = new LineSegment(smallest, largest);

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
     * we will not supply any input to FastCollinearPoints that has 5 or more
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
    }
}
