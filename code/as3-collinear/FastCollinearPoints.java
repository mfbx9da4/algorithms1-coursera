import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdDraw;
import java.util.Arrays;

public class FastCollinearPoints {

    private LineSegment[] segments;
    private int size;

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

        Point smallest;
        Point largest;
        segments = new LineSegment[1];
        size = 0;

        // copy to auxillary array for sorting
        Point[] aux = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            aux[i] = points[i];
        }


        for (int i = 0; i < points.length - 1; i++) {
            // For each other point q, determine the slope it makes with p.
            // Sort the points according to the slopes they makes with p.
            // Check if any 3 (or more) adjacent points in the sorted order have equal
            // slopes with respect to p. If so, these points, together with p, are
            // collinear.
            // Applying this method for each of the N points

            Point p = points[i];
            System.out.println("Next p " + i + " " + p);
            if (p == null) throw new java.lang.NullPointerException();
            Arrays.sort(aux, p.slopeOrder());

            // init counter for first slope for first q
            System.out.println("First q " + aux[i+1]);
            double prev_slope = p.slopeTo(aux[i+1]);
            int collinear_count = 1;

            // all other q
            for (int j = i+2; j < aux.length; j++) {
                Point q = aux[j];
                double p_to_q = p.slopeTo(q);
                if (p_to_q == Double.NEGATIVE_INFINITY) throw new java.lang.IllegalArgumentException();

                else if (p_to_q == prev_slope) {
                    // same slope as adjacent slope
                    collinear_count++;
                    System.out.println("Incremented to " + collinear_count + " current q " + q);
                } else {
                    // q is not part of line segment
                    System.out.println("Not adjacent " + collinear_count + " current q " + q);

                    if (collinear_count > 2) {
                        // found line segment
                        System.out.println("/*----------  Found line segment  ----------*/");

                        Point[] segment_points = getSegmentPoints(aux, i, j - 1, collinear_count);

                        addLineSegment(segment_points);

                    }

                    // reset counter
                    prev_slope = p_to_q;
                    collinear_count = 1;
                }

            }
        }

    }

    /**
     *
     * @param  origin_idx       Index of origin point from which segment was found.
     * @param  last_idx         The index of the last point known to be part of this segment.
     * @param  collinear_count  Count of collinear points excluding origin.
     *
     */
    private Point[] getSegmentPoints(Point[] aux, int origin_idx, int last_idx, int collinear_count) {
        Point[] segment_points = new Point[collinear_count + 1];

        // Put the origin at the end of the array of points
        segment_points[collinear_count] = aux[origin_idx];
        System.out.println("origin: " + aux[origin_idx]);

        System.out.println("count " + collinear_count);
        // Goes from last point of segment to beginning
        for (int k = 0; k < collinear_count; k++) {
            System.out.println(last_idx + ", " + k + "==>" + (last_idx - k));
            segment_points[k] = aux[last_idx - k];
            System.out.println("point: " + segment_points[k]);
        }

        return segment_points;

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

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {
    }
}
