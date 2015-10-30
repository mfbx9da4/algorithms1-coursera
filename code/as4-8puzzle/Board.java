import edu.princeton.cs.algs4.In;
import java.util.Iterator;
import java.util.Arrays;


public class Board {
    private int[][] blocks;
    private boolean verbose = false;
    private int manhattan_score = 0;
    private int hamming_score = 0;
    private MatrixIndex free;

    /**
     *
     * Helper class for matrix indices.
     *
     */
    private class MatrixIndex {
        public int i;
        public int j;

        public MatrixIndex(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public void reset(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public String toString() {
            return this.i + "," + this.j;
        }
    }

    /**
     *
     * Construct a board from an N-by-N array of blocks and calculate hamming
     * and manhattan scores.
     *
     *
     */
    public Board(int[][] blocks) {
        this.blocks = new int[blocks.length][blocks.length];

        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                this.blocks[i][j] = blocks[i][j];
                updateHammingScore(i, j);
                updateManhattanScore(i, j);
            }
        }

    }

    /**
     *
     * Update manhattan score for particular block location i j. Calculate goal
     * position for value, sum vertical and horizontal distance from goal
     * position and add to manhattan score. If is the free cell, save its
     * location and don't calulcate score.
     *
     * @param i Block row position.
     * @param j Block col position.
     *
     */
    private void updateManhattanScore(int i, int j) {
        int value = blocks[i][j];
        if (value == 0) {
            // save MatrixIndex of free square
            free = new MatrixIndex(i, j);
        } else {
            // calculate goal position for this value
            int goal_i = (value - 1) / dimension();
            int goal_j = (value - 1) % dimension();

            // calculate vertical and horizontal distances
            int distance = Math.abs(i - goal_i) + Math.abs(j - goal_j);

            // update manhattan_score
            manhattan_score += distance;

            if (verbose) {
                // System.out.println(value + " from " + i + "," + j + " to " + goal_i + "," + goal_j + " = " + distance);
            }
        }
    }

    /**
     *
     * Update hamming score for particular block position, if it is not the free
     * cell. Incremeent score if block not in correct cell.
     *
     * @param i Block row position.
     * @param j Block col position.
     *
     */
    private void updateHammingScore(int i, int j) {
        // if not free cell
        if (!(i == dimension() - 1 && j == dimension() - 1)) {
            // calculate goal value for this cell
            int goal = i * dimension() + j + 1;
            if (blocks[i][j] != goal) {
                hamming_score ++;
            }

            if (verbose) {
                // System.out.println(i + "," + j + " " + block + " is " + goal + "?" );
            }
        }
    }

    public int dimension() {
        // board dimension N
        return blocks.length;
    }

    public int hamming() {
        // number of blocks out of place
        return hamming_score;
    }

    public int manhattan() {
        // sum of Manhattan distances between blocks and goal
        return manhattan_score;
    }

    public boolean isGoal() {
        // is this board the goal board?
        return hamming() == 0;
    }

    /**
     *
     * A board that is obtained by exchanging any pair of blocks. Find first
     * pair of non free blocks and createMovedBoard exchanging these blocks.
     *
     * @return A board with a pair of exchanged blocks.
     *
     */

    public Board twin() {
        MatrixIndex nonFreeBlock1 = new MatrixIndex(0, 0);
        findNextBlock(nonFreeBlock1);

        MatrixIndex nonFreeBlock2 = new MatrixIndex(nonFreeBlock1.i, nonFreeBlock1.j);
        incrementMatrixIndex(nonFreeBlock2);
        findNextBlock(nonFreeBlock2);

        if (verbose) {
            System.out.println(nonFreeBlock1 + " " + nonFreeBlock2);
        }
        return createMovedBoard(nonFreeBlock1, nonFreeBlock2);
    }

    private boolean isFreeBlock(MatrixIndex loc) {
        return loc.i == free.i && loc.j == free.j;
    }

    private boolean inBounds(MatrixIndex loc) {
        return loc.i >= 0 && loc.i < dimension() && loc.j >= 0 && loc.j < dimension();
    }

    private void incrementMatrixIndex(MatrixIndex loc) {
        int block = loc.i * dimension() + loc.j + 1;
        loc.reset((block) / dimension(), (block) % dimension());
    }

    private void findNextBlock(MatrixIndex cursor) {
        // ensures is not free block
        while (isFreeBlock(cursor) || !inBounds(cursor)){
            incrementMatrixIndex(cursor);
        }
    }

    /**
     *
     * Exchanges src, dest indicies specified, creates board, switches blocks
     * back to original positions and returns board.
     *
     * @param src Source index.
     * @param dest Destination index.
     * @return Returns board where src and destination indices have been
     *         swapped.
     *
     */
    private Board createMovedBoard(MatrixIndex src, MatrixIndex dest) {
        int srcVal = blocks[src.i][src.j];
        blocks[src.i][src.j] = blocks[dest.i][dest.j];
        blocks[dest.i][dest.j] = srcVal;
        Board movedBoard = new Board(blocks);
        blocks[dest.i][dest.j] = blocks[src.i][src.j];
        blocks[src.i][src.j] = srcVal;
        return movedBoard;
    }

    public boolean equals(Object y) {
        // does this board equal y?

        if (y == this) {
            return true;
        }
        if (y == null) {
            return false;
        }
        if (y.getClass() != this.getClass()) {
            return false;
        }

        Board that = (Board) y;

        if (dimension() != that.dimension()) {
            return false;
        }

        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                if (blocks[i][j] != that.blocks[i][j]) {
                    return false;
                }
            }

        }

        return true;
    }

    public Iterable<Board> neighbors() {
        // all neighboring boards
        return new Neighbors();
    }

    private class Neighbors implements Iterable<Board> {
        public Iterator<Board> iterator() {
            return new NeighborIterator();
        }

        private class NeighborIterator implements Iterator<Board> {

            private int directions_checked = 0;
            private int left = free.j - 1;
            private int right = free.j + 1;
            private int top = free.i - 1;
            private int bottom = free.i + 1;
            private MatrixIndex move = new MatrixIndex(-1, -1);
            private Board current;
            private Board next = nextBoard();


            private boolean hasExhaustedDirections() {
                return directions_checked >= 4;
            }

            private void getNextMove() {
                if (directions_checked == 0) {
                    move.reset(free.i, left);
                } else if (directions_checked == 1) {
                    move.reset(free.i, right);
                } else if (directions_checked == 2) {
                    move.reset(top, free.j);
                } else if (directions_checked == 3) {
                    move.reset(bottom, free.j);
                }
            }

            /**
             *
             * Check if move in particular direction is in bounds. If in bounds
             * create moved board, else try next direction until exhausted all
             * directions.
             *
             * @return moved board in a particular direction.
             *
             */
            private Board nextBoard () {
                while (!hasExhaustedDirections()) {
                    getNextMove();
                    directions_checked++;
                    if (inBounds(move)) {
                        return createMovedBoard(free, move);
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

            public Board next() {

                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }

                current = next;
                next = nextBoard();
                return current;
            }
        }

    }

    public String toString() {
        // string representation of this board (in the output format specified below)
        String output = dimension() + "\n";
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                if (blocks[i][j] < 10) {
                    output += " ";
                }
                output += blocks[i][j] + " ";
            }
            output += "\n";
        }
        return output;
    }


    public static void main(String[] args) {
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                blocks[i][j] = in.readInt();
            }
        }
        System.out.println(Arrays.deepToString(blocks));

        Board board = new Board(blocks);
        System.out.println(board);
        System.out.println("Board dimensions: " + board.dimension() + "x" + board.dimension());
        System.out.println("Hamming score: " + board.hamming());
        System.out.println("Manhattan score: " + board.manhattan());
        for (Board neighbor : board.neighbors()) {
            System.out.println(neighbor);
        }

        /*----------  Unit tests  ----------*/

        blocks = new int [][] {
            {4, 1, 3},
            {0, 2, 6},
            {7, 5, 8}
        };

        String[] neighbors = new String[] {
            " 4  1  3 \n 2  0  6 \n 7  5  8 \n",
            " 0  1  3 \n 4  2  6 \n 7  5  8 \n",
            " 4  1  3 \n 7  2  6 \n 0  5  8 \n"
        };

        board = new Board(blocks);
        assert board.dimension() == 3;
        assert board.isGoal() == false;
        assert board.hamming() == 6;
        assert board.manhattan() == 8;
        int i = 0;
        for (Board neighbor : board.neighbors()) {
            assert neighbor.toString() == neighbors[i];
            i++;
        }

        assert board.twin().blocks == new int [][] {{1, 4, 3}, {0, 2, 6}, {7, 5, 8}};
        assert board.twin() == new Board(new int [][] {{1, 4, 3}, {0, 2, 6}, {7, 5, 8}});

        System.out.println("All tests passed :-)");
    }

}
