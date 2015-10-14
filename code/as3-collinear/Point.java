/******************************************************************************
 *  Compilation:  javac Point.java
 *  Execution:    java Point
 *  Dependencies: none
 *
 *  An immutable data type for points in the plane.
 *  For use on Coursera, Algorithms Part I programming assignment.
 *
 ******************************************************************************/

import java.util.Comparator;
import edu.princeton.cs.algs4.StdDraw;

public class Point implements Comparable<Point> {

    private final int x;     // x-coordinate of this point
    private final int y;     // y-coordinate of this point

    /**
     * Initializes a new point.
     *
     * @param  x the <em>x</em>-coordinate of the point
     * @param  y the <em>y</em>-coordinate of the point
     */
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    /**
     * Draws the line segment between this point and the specified point
     * to standard draw.
     *
     * @param that the other point
     */
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Returns the slope between this point and the specified point.
     * Formally, if the two points are (x0, y0) and (x1, y1), then the slope
     * is (y1 - y0) / (x1 - x0). For completness, the slope is defined to be
     * +0.0 if the line segment connecting the two points is horizontal;
     * Double.POSITIVE_INFINITY if the line segment is vertcal;
     * and Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1) are equal.
     *
     * @param  that the other point
     * @return the slope between this point and the specified point
     */
    public double slopeTo(Point that) {
        double dy = this.y - that.y;
        double dx = this.x - that.x;

        if (dx == 0 && dy == 0) {
            // degenerate
            return Double.NEGATIVE_INFINITY;
        } else if (dy == 0) {
            // horizontal
            return 0;
        } else if (dx == 0) {
            // vertical
            return Double.POSITIVE_INFINITY;
        } else {
            // slope
            return dy / dx;
        }
    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate.
     * Formally, the invoking point (x0, y0) is less than the argument point
     * (x1, y1) if and only if either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param  that the other point
     * @return the value <tt>0</tt> if this point is equal to the argument
     *         point (x0 = x1 and y0 = y1);
     *         a negative integer if this point is less than the argument
     *         point; and a positive integer if this point is greater than the
     *         argument point
     */
    public int compareTo(Point that) {
        if (this.y < that.y) {
            // less
            return -1;
        } else if (this.y > that.y) {
            // greater
            return 1;
        } else if (this.x < that.x) {
            return -1;
        } else if (this.x > that.x) {
            return 1;
        }
        return 0;
    }

    /**
     * Compares two points by the slope they make with this point.
     * The slope is defined as in the slopeTo() method.
     *
     * @return the Comparator that defines this ordering on points
     */
    public Comparator<Point> slopeOrder() {
        return new SlopeOrder();
    }

    /**
     *
     * SlopeOrder comparator
     *  Compares its two argument points by the slopes they make
     *  with the invoking point (x0, y0). Formally, the point (x1, y1) is
     *  less than the point (x2, y2) if and only if the slope
     *  (y1 − y0) / (x1 − x0) is less than the slope (y2 − y0) / (x2 − x0).
     *  Treat horizontal, vertical, and degenerate line segments as in the
     *  slopeTo() method.
     *
     */
    private class SlopeOrder implements Comparator<Point> {
        public int compare(Point p, Point q) {
            double to_p = slopeTo(p);
            double to_q = slopeTo(q);
            if (to_p < to_q) {
                return -1;
            } else if (to_p > to_q) {
                return 1;
            }
            return 0;
        }
    }


    /**
     * Returns a string representation of this point.
     * This method is provide for debugging;
     * your program should not rely on the format of the string representation.
     *
     * @return a string representation of this point
     */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {
        Point p = new Point(4, 5);
        Point q = new Point(4, 5);
        p.draw();
        q.draw();

        /*----------  Test compareTo  ----------*/

        assert p.compareTo(q) == 0;

        q = new Point(3, 5);
        assert p.compareTo(q) == 1;

        q = new Point(6, 5);
        assert p.compareTo(q) == -1;

        q = new Point(4, 4);
        assert p.compareTo(q) == 1;

        q = new Point(4, 6);
        assert p.compareTo(q) == -1;

        /*----------  Test slopeTo  ----------*/

        p = new Point(4, 5);

        // vertical
        q = new Point(4, 6);
        assert p.slopeTo(q) == Double.POSITIVE_INFINITY;
        q = new Point(4, 2);
        assert p.slopeTo(q) == Double.POSITIVE_INFINITY;

        // horizontal
        q = new Point(6, 5);
        assert p.slopeTo(q) == 0;
        q = new Point(1, 5);
        assert p.slopeTo(q) == 0;

        // degenerate
        q = new Point(4, 5);
        assert p.slopeTo(q) == Double.NEGATIVE_INFINITY;

        q = new Point(1, 11);
        assert p.slopeTo(q) == -2;

        q = new Point(2, 3);
        assert p.slopeTo(q) == 1;

        q = new Point(7, 14);
        assert p.slopeTo(q) == 3;


        /*----------  Test slopeOrder  ----------*/

        p = new Point(2, 2);
        q = new Point(3, 5);
        Point r = new Point(0, 1);
        Comparator<Point> cmp = p.slopeOrder();

        // 3 > 0.5
        assert cmp.compare(q, r) == 1;
        // 0.5 < 3
        assert cmp.compare(q, q) == 0;

        q = new Point(3, 2);
        r = new Point(2, 3);
        // 0 < infin
        assert cmp.compare(q, r) == -1;
        // infin > 0
        assert cmp.compare(r, q) ==  1;
        // -infin < 0
        assert cmp.compare(p, q) == -1;
        // -infin < infin
        assert cmp.compare(p, r) == -1;

        System.out.println("All tests passed :-)");

    }
}
