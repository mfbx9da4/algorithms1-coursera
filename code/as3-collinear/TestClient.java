import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;
import java.util.Arrays;

public class TestClient {
    public static void main(String[] args) {
        // read the N points from a file
        In in = new In(args[0]);
        int N = in.readInt();
        Point[] points = new Point[N];
        for (int i = 0; i < N; i++) {
            int x = in.readInt();
            int y = in.readInt();
            System.out.println("Point: " + x + ", " + y);
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.show(0);
        StdDraw.setCanvasSize(1000, 1000);
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            StdDraw.setPenRadius(.025);
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        // BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        int i = 0;
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            StdDraw.setPenRadius(.01);
            StdDraw.setPenColor(StdRandom.uniform(255), StdRandom.uniform(255), StdRandom.uniform(255));
            StdDraw.text(15000, 1000 * ++i, segment.toString());
            segment.draw();
        }
    }
}
