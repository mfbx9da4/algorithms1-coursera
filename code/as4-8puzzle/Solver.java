import java.util.Iterator;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;


public class Solver {
    private MinPQ<SearchNode> queue = new MinPQ<SearchNode>();
    private MinPQ<SearchNode> twinQueue = new MinPQ<SearchNode>();
    private SearchNode goalNode;
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

    public Solver(Board initialBoard)           {
        // find a solution to the initial board (using the A* algorithm)
        if (initialBoard == null) throw new java.lang.NullPointerException();
        SearchNode initial = new SearchNode(initialBoard, null, 0);
        SearchNode initialTwin = new SearchNode(initialBoard.twin(), null, 0);
        queue.insert(initial);
        twinQueue.insert(initialTwin);
        solve();
    }

    private void solve() {
        while (true) {
            SearchNode current = queue.delMin();
            SearchNode currentTwin = twinQueue.delMin();

            if (current.board.isGoal()) {
                // solved
                goalNode = current;
                isSolvable = true;
                break;
            } else if (currentTwin.board.isGoal()) {
                // twin solved
                isSolvable = false;
                break;
            } else {
                // enqueue neighbors
                moves = current.moves + 1;
                enqueueNeighbors(queue, current);
                enqueueNeighbors(twinQueue, currentTwin);
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

            private int i;
            private Board current;
            private Board next;
            private int totalBoards = moves + 1; // +1 for first board
            private Board[] partials = new Board[totalBoards];


            public PartialsIterator() {
                i = 1;
                SearchNode cur = goalNode;
                while (i < totalBoards + 1) {
                    partials[totalBoards - i++] = cur.board;
                    cur = cur.previous;
                }
                i = 0;
            }


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
