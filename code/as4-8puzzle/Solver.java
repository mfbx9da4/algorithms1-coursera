import java.util.Iterator;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;


public class Solver {
    private MinPQ<SearchNode> queue = new MinPQ<SearchNode>();
    private MinPQ<SearchNode> twin_queue = new MinPQ<SearchNode>();
    private Board[] partials = new Board[2];
    private int moves;
    private boolean isSolvable;

    private final class SearchNode implements Comparable<SearchNode> {
        public Board board;
        public SearchNode previous;
        public int moves;

        public SearchNode(Board board, SearchNode previous, int moves) {
            this.board = board;
            this.previous = previous;
            this.moves = moves;
        }

        public int priority() {
            return moves + board.manhattan() + board.hamming();
        }

        public int compareTo(SearchNode that) {
            int this_score = this.priority();
            int that_score = that.priority();
            if (this_score < that_score) {
                return -1;
            } else if (this_score > that_score) {
                return 1;
            }
            return 0;
        }

    }

    public Solver(Board initial_board)           {
        // find a solution to the initial board (using the A* algorithm)
        if (initial_board == null) throw new java.lang.NullPointerException();
        SearchNode initial = new SearchNode(initial_board, null, 0);
        SearchNode initial_twin = new SearchNode(initial_board.twin(), null, 0);
        queue.insert(initial);
        twin_queue.insert(initial_twin);
        solve();
    }

    private void saveNode(SearchNode node) {
        resizeArray();
        int i = node.moves;
        partials[i] = node.board;
        System.out.println(node.board + " Moves " + i);
    }

    private void resizeArray () {
        // resize array
        if (moves == partials.length) {
            Board[] cp = new Board[partials.length * 2];
            for (int m = 0; m < partials.length; m++) {
                cp[m] = partials[m];
            }
            partials = cp;
        }
    }


    private void solve() {
        while (true) {
            SearchNode current = queue.delMin();
            SearchNode current_twin = twin_queue.delMin();
            saveNode(current);

            if (current.board.isGoal()) {
                // solved
                isSolvable = true;
                break;
            } else if (current_twin.board.isGoal()) {
                // twin solved
                isSolvable = false;
                break;
            } else {
                // enqueue neighbors
                moves = current.moves + 1;
                enqueueNeighbors(queue, current);
                enqueueNeighbors(twin_queue, current_twin);
            }
        }
    }

    private void enqueueNeighbors(MinPQ<SearchNode> queue, SearchNode current) {
        for (Board neighbor : current.board.neighbors()) {
            if (current.previous == null || neighbor != current.previous.board) {
                queue.insert(new SearchNode(neighbor, current, moves));
            }
        }
    }

    public boolean isSolvable()            {
        // is the initial board solvable?
        return isSolvable;
    }

    public int moves()                     {
        // min number of moves to solve initial board; -1 if unsolvable
        if (isSolvable) {
            return moves;
        } else {
            return -1;
        }
    }

    public Iterable<Board> solution()      {
        // sequence of boards in a shortest solution; null if unsolvable
        if (isSolvable) {
            return new PartialsIterable();
        } else {
            return null;
        }
    }

    private class PartialsIterable implements Iterable<Board> {
        public Iterator<Board> iterator() {
            return new PartialsIterator();
        }

        private class PartialsIterator implements Iterator<Board> {

            private int i = 0;
            private Board current;
            private Board next = partials[i];


            public boolean hasNext() {
                return partials.length > i && partials[i] != null;
            }

            public void remove() {
                throw new java.lang.UnsupportedOperationException();
            }

            public Board next() {

                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                return partials[i++];
            }
        }

    }


    public static void main(String[] args) {
        // solve a slider puzzle (given below)
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                blocks[i][j] = in.readInt();
            }
        }

        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible " + solver.moves());
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}
