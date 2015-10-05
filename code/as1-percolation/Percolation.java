import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private int[][] grid;
    private WeightedQuickUnionUF wquf;
    private int size;

    public Percolation(int N) {
        // create N-by-N grid, with all sites blocked
        if (N <= 0) {
            throw new java.lang.IllegalArgumentException();
        }

        size = N;
        grid = new int[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                grid[i][j] = 0;
            }
        }

        // create wquf
        wquf = new WeightedQuickUnionUF(N*N + 2);

        // create union from auxilary start node to first row
        for (int i = 1; i <= N; i++) {
            wquf.union(0, i);
        }

        // create union from auxilary end node to last row
        int first_of_last_row = (N*N) - N + 1;
        for (int i = first_of_last_row; i <= N*N; i++) {
            wquf.union(N*N + 1, i);
        }
    }

    public void open(int i, int j) {
        // public use 1,1 api
        i = i - 1;
        j = j - 1;

        // open site (row i, column j) if it is not open already
        grid[i][j] = 1;

        // check if neighbours are open if so union
        connectToOpenNeighbours(i, j);
    }

    private void connectToOpenNeighbours(int i, int j) {
        // check if neighbours are open if so union

        int node1 = convertCoordsToIndex(i, j);

        int left = i - 1;
        int right = i + 1;

        for (int p : new int[] {left, right}) {
            if (coordsInRange(p, j) && isOpen(p + 1, j + 1)) {
                int node2 = convertCoordsToIndex(p, j);
                wquf.union(node1, node2);
            }
        }

        int top = j - 1;
        int bottom = j + 1;

        for (int p : new int[] {top, bottom}) {
            if (coordsInRange(i, p) && isOpen(i + 1, p + 1)) {
                int node2 = convertCoordsToIndex(i, p);
                wquf.union(node1, node2);
            }
        }

    }

    private boolean coordsInRange(int i, int j) {
        boolean i_in_range = (i >= 0) && (i < size);
        boolean j_in_range = (j >= 0) && (j < size);
        return i_in_range && j_in_range;
    }

    public boolean isOpen(int i, int j) {
        // is site (row i, column j) open?
        return grid[i - 1][j - 1] > 0;
    }

    public boolean isFull(int i, int j) {
        // is site (row i, column j) full?
        int node_index = convertCoordsToIndex(i - 1, j - 1);
        return isOpen(i, j) && wquf.connected(0, node_index);
    }

    private int root(int i, int j) {
        int node_index = convertCoordsToIndex(i - 1, j - 1);
        return wquf.find(node_index);
    }

    public boolean percolates() {
        // does the system percolate?
        int end = (size * size) + 1;
        return wquf.connected(0, end);
    }

    private int convertCoordsToIndex(int i, int j) {
        return (i * size) + (j + 1);
    }

    public static void main(String[] args)  {
        // test client (optional)
        Percolation perc = new Percolation(3);

        // assert first and last rows connected to
        // start and end nodes
        assert perc.wquf.connected(0, 1);
        assert perc.wquf.connected(0, 2);
        assert perc.wquf.connected(0, 3);
        assert perc.wquf.connected(10, 7);
        assert perc.wquf.connected(10, 8);
        assert perc.wquf.connected(10, 9);
    }

}
