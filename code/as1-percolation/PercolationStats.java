import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private double[] thresholds;

    public PercolationStats(int N, int T) {
        // perform T independent experiments on an N-by-N grid

        if (N <= 0 || T <= 0) {
            throw new java.lang.IllegalArgumentException();
        }

        String message = "Perform " + T + " trials for a " + N + "x" + N + " grid";
        System.out.println(message);

        thresholds = new double[T];

        for (int t = 0; t < T; t++) {

            Percolation perc = new Percolation(N);
            int opened = 0;

            while (!perc.percolates()) {
                int x = StdRandom.uniform(N) + 1;
                int y = StdRandom.uniform(N) + 1;
                if (!perc.isOpen(x, y)) {
                    perc.open(x, y);

                    opened++;
                }
            }
            double threshold = ((double) opened) / (N * N);
            thresholds[t] = threshold;
            message = "Finished trial " + t + ", opened " + opened;
            message = message + " sites of " + N*N;
            message = message + " giving threshold: " + threshold;
            System.out.println(message);
        }

        System.out.println("mean: " + mean());
        System.out.println("stddev: " + stddev());
        System.out.println("confidenceLo: " + confidenceLo());
        System.out.println("confidenceHi: " + confidenceHi());

    }

    public double mean() {
        // sample mean of percolation threshold
        return StdStats.mean(thresholds);
    }

    public double stddev() {
        // sample standard deviation of percolation threshold
        return StdStats.stddev(thresholds);
    }

    public double confidenceLo() {
        // low  endpoint of 95% confidence interval
        return mean() - ((1.96 * stddev()) / Math.sqrt(thresholds.length));
    }

    public double confidenceHi() {
        // high endpoint of 95% confidence interval
        return mean() + ((1.96 * stddev()) / Math.sqrt(thresholds.length));
    }

    public static void main(String[] args) {
        // test client (described below)
        new PercolationStats(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }

}
