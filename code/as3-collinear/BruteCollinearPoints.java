import edu.princeton.cs.algs4.StdRandom;


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
        Point smallest;
        Point largest;
        segments = new LineSegment[1];
        size = 0;

        for (int i = 0; i < points.length; i++) {
            Point p = points[i];

            outerloop:
            for (int j = i + 1; j < points.length; j++) {
                Point q = points[j];
                double p_to_q = p.slopeTo(q);

                for (int k = j + 1; k < points.length; k++) {
                    Point r = points[k];
                    double p_to_r = p.slopeTo(r);

                    // don't bother checking s, already not collinear
                    if (p_to_q != p_to_r) continue;

                    for (int l = 0; l < points.length; l++) {
                        Point s = points[l];
                        double p_to_s = p.slopeTo(s);

                        if (p_to_s == p_to_q) {
                            System.out.println("Found line segment");
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
                            System.out.println(size);

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
        return 0;
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
        return segments;
    }

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {
        Point[] points = new Point[30];
        for (int i = 0; i < points.length; i++) {
            int x = StdRandom.uniform(30);
            int y = StdRandom.uniform(30);
            points[i] = new Point(x, y);
        }

        new BruteCollinearPoints(points);
    }
}
