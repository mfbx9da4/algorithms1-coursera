import edu.princeton.cs.algs4.In;
import java.util.Iterator;
import java.util.Arrays;



final public class Board {
    private int[][] blocks;
    private boolean verbose = false;
    private int manhattan_score = 0;
    private int hamming_score = 0;
    private int free_i;
    private int free_j;
    private Location free;

    private final class Location {
        public final int i;
        public final int j;

        public Location (int i, int j) {
            this.i = i;
            this.j = j;
        }

        public String toString() {
            return this.i + "," + this.j;
        }
    }

    public Board(int[][] blocks) {
        // construct a board from an N-by-N array of blocks
        // (where blocks[i][j] = block in row i, column j)
        this.blocks = blocks;

        int goal_i;
        int goal_j;
        int goal;
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                int block = blocks[i][j];

                // update hamming score
                if (!(i == dimension() - 1 && j == dimension() - 1)) {
                    // calculate goal value for this cell
                    goal = i * dimension() + j + 1;
                    if (verbose) {
                        System.out.println(i + "," + j + " " + block + " is " + goal + "?" );
                    }
                    if (block != goal) {
                        hamming_score ++;
                    }
                }

                if (block == 0) {
                    // save location of free square
                    free = new Location(i, j);
                    free_i = i;
                    free_j = j;
                } else {
                    // calculate goal position for this value
                    goal_i = (block - 1) / dimension();
                    goal_j = (block - 1) % dimension();

                    // calculate vertical and horizontal distances
                    int distance = Math.abs(i - goal_i) + Math.abs(j - goal_j);

                    // update manhattan_score
                    manhattan_score += distance;

                    if (verbose) {
                        System.out.println(block + " from " + i + "," + j + " to " + goal_i + "," + goal_j + " = " + distance);
                    }
                }


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

    public Board twin() {
        // a board that is obtained by exchanging any pair of blocks
        Location src = findNextBlock(new Location(0, 0));
        Location dest = findNextBlock(incrementLocation(src));
        return createMovedBoard(src, dest);
    }

    private boolean isFreeBlock(Location loc) {
        return loc.i == free.i && loc.j == free.j;
    }

    private boolean inBounds(Location loc) {
        return loc.i >= 0 && loc.i < dimension() && loc.j >= 0 && loc.j < dimension();
    }

    private Location incrementLocation(Location loc) {
        int block = loc.i * dimension() + loc.j + 1;
        return new Location((block) / dimension(), (block) % dimension());
    }

    private Location findNextBlock(Location cursor) {
        // ensures is not free block
        while (isFreeBlock(cursor) || !inBounds(cursor)){
            cursor = incrementLocation(cursor);
        }
        return cursor;
    }

    private Board createMovedBoard(Location src, Location dest) {
        return createMovedBoard(src.i, src.j, dest.i, dest.j);
    }

    private Board createMovedBoard(int src_i, int src_j, int dest_i, int dest_j) {
        int[][] twin_blocks = new int[dimension()][dimension()];
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                twin_blocks[i][j] = blocks[i][j];
            }
        }
        twin_blocks[src_i][src_j] = blocks[dest_i][dest_j];
        twin_blocks[dest_i][dest_j] = blocks[src_i][src_j];
        return new Board(twin_blocks);
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
            // private positions;
            private int left = free_j - 1;
            private int right = free_j + 1;
            private int top = free_i - 1;
            private int bottom = free_i + 1;
            private Board current;
            private Board next = nextBoard();


            private boolean hasNextMove() {
                return directions_checked < 4;
            }

            private int[] getNextMove() {
                int[] move = new int[2];
                move[0] = free_i;
                move[1] = free_j;

                if (directions_checked == 0) {
                    move[1] = left;
                } else if (directions_checked == 1) {
                    move[1] = right;
                } else if (directions_checked == 2) {
                    move[0] = top;
                } else if (directions_checked == 3) {
                    move[0] = bottom;
                }
                return move;
            }

            private boolean moveInBounds(int[] move) {
                return move[0] >= 0 && move[0] < dimension() && move[1] >= 0 && move[1] < dimension();
            }

            private Board nextBoard () {
                while (hasNextMove()) {
                    int[] move = getNextMove();
                    directions_checked++;
                    if (moveInBounds(move)) {
                        return createMovedBoard(free_i, free_j, move[0], move[1]);
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
