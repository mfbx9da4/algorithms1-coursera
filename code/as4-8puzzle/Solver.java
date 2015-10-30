import java.util.Iterator;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;


public class Solver {
    private MinPQ<SearchNode> initialHeap = new MinPQ<SearchNode>();
    private MinPQ<SearchNode> twinHeap = new MinPQ<SearchNode>();
    private SearchNode goalNode;
    private boolean isSolvable;
    private boolean verbose = false;

    /**
     *
     * Each search node of A* algorithm, represents
     * a specific board and has a link to its parent
     * board (previous) and the number of moves to
     * reach this specific board.
     *
     */
    private static final class SearchNode implements Comparable<SearchNode> {
        private Board board;
        private SearchNode previous;
        private int moves;

        /**
         *
         * Constructor.
         *
         * @param board specific board for this search node
         * @param previous link to parent node
         * @param moves number of moves since start to reach this node
         *
         */
        public SearchNode(Board board, SearchNode previous, int moves) {
            this.board = board;
            this.previous = previous;
            this.moves = moves;
        }



        /**
         *
         * Critical priority function for determining priority
         * of SearchNode in priority queue.
         *
         * Hamming priority: Number of blocks in wrong position + number of
         *                   moves.
         *
         * Manhattan priority: Sum of vertical + horizontal distances of blocks
         *                     to their goal positions + number of moves.
         *
         * Total moves to solve the puzzle for a given search node is at least
         * manhattan or priority function.
         *
         */
        public int priority() {
            return moves + board.manhattan();
        }

        /**
         *
         * Compares two search nodes.
         *
         * @param  that The other search node.
         *
         */
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


    /**
     *
     * Find a solution to the initial board (using the A* algorithm). A twin
     * board is created and solved in parallel because the initial board may not
     * be solvable and if this is the case the twin board will be solvable.
     * The boards are inserted onto their respective heaps and the solve method
     * recurses on their neighbors.
     *
     * @param initialBoard the starting board.
     *
     */
    public Solver(Board initialBoard) {
        if (initialBoard == null) throw new java.lang.NullPointerException();
        SearchNode initial = new SearchNode(initialBoard, null, 0);
        SearchNode twin = new SearchNode(initialBoard.twin(), null, 0);
        if (verbose) {
            System.out.println("Starting board\n" + initial.board);
            System.out.println("Twin\n" + twin.board);
        }
        initialHeap.insert(initial);
        twinHeap.insert(twin);
        solve();
    }

    /**
     *
     * The main A* algorithm. The node with the smallest priority is removed
     * from the heap and we insert its neighbors on to the heap. We perform this
     * for both initial board and twin board. We stop when the initial or twin
     * board reaches the goal board.
     *
     */
    private void solve() {
        while (true) {
            // dequeue node with smallest priority
            SearchNode initial = initialHeap.delMin();
            SearchNode twin = twinHeap.delMin();

            if (initial.board.isGoal()) {
                // solved
                goalNode = initial;
                isSolvable = true;
                break;
            } else if (twin.board.isGoal()) {
                // twin solved
                isSolvable = false;
                break;
            } else {
                // enheap neighbors
                enheapNeighbors(initialHeap, initial);
                enheapNeighbors(twinHeap, twin);
            }
        }
    }

    /**
     *
     * Inserts neighboring boards of a current node's board, provided the
     * neighbor is not equal to the current node's parent board. If the current
     * board is the initial board, ie took 0 moves, it has no parent and we
     * ignore this check. Here we always increment the number of moves based on
     * the number of moves to get to the parent.
     *
     * @param heap Min heap to insert onto (inital/twin).
     * @param current The current select node.
     *
     */
    private void enheapNeighbors(MinPQ<SearchNode> heap, SearchNode current) {
        for (Board neighbor : current.board.neighbors()) {
            if (current.moves == 0 || neighbor != current.previous.board) {
                SearchNode neighborNode = new SearchNode(neighbor, current, current.moves + 1);
                heap.insert(neighborNode);
            }
        }
    }

    /**
     *
     * @return Is the inital board solvable.
     *
     */
    public boolean isSolvable() {
        return isSolvable;
    }

    /**
     *
     * @return min number of moves to solve initial board; -1 if unsolvable
     *
     */
    public int moves() {
        if (isSolvable) {
            return goalNode.moves;
        } else {
            return -1;
        }
    }

    /**
     *
     * @return sequence of boards in a shortest solution; null if unsolvable
     *
     */
    public Iterable<Board> solution() {
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
            private int totalBoards = goalNode.moves + 1; // +1 for first board
            private Board[] partials = new Board[totalBoards];

            /**
             *
             * Constructor creates array of moves tracing back from goalBoard
             * to first node.
             *
             */
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
            int i = 0;
            for (Board board : solver.solution()) {
                System.out.println(i++);
                StdOut.println(board);
            }
        }
    }
}

