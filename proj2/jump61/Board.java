package jump61;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Formatter;

import java.util.Stack;
import java.util.function.Consumer;

import static jump61.Side.*;

/** Represents the state of a Jump61 game.  Squares are indexed either by
 *  row and column (between 1 and size()), or by square number, numbering
 *  squares by rows, with squares in row 1 numbered from 0 to size()-1, in
 *  row 2 numbered from size() to 2*size() - 1, etc. (i.e., row-major order).
 *
 *  A Board may be given a notifier---a Consumer<Board> whose
 *  .accept method is called whenever the Board's contents are changed.
 *
 *  @author Marcus Cheung
 */
class Board {

    /** An uninitialized Board.  Only for use by subtypes. */
    protected Board() {
        _notifier = NOP;
    }

    /** Boardstate. */
    private Square[][] state;

    /** History. */
    private Stack<Square[][]> history;

    /** An N x N board in initial configuration. */
    Board(int N) {
        this();
        state = new Square[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                state[i][j] = Square.square(Side.WHITE, 1);
            }
        }
        history = new Stack<>();
    }

    /** A board whose initial contents are copied from BOARD0, but whose
     *  undo history is clear, and whose notifier does nothing. */
    Board(Board board0) {
        this(board0.size());
        for (int i = 0; i < board0.size(); i++) {
            for (int j = 0; j < board0.size(); j++) {
                Square s = board0.get(i + 1, j + 1);
                state[i][j] = Square.square(s.getSide(), s.getSpots());
            }
        }
        _notifier = NOP;
        _readonlyBoard = new ConstantBoard(this);
    }

    /** Returns a readonly version of this board. */
    Board readonlyBoard() {
        return _readonlyBoard;
    }

    /** (Re)initialize me to a cleared board with N squares on a side. Clears
     *  the undo history and sets the number of moves to 0. */
    void clear(int N) {
        Board b = new Board(N);
        state = b.state;
        history = new Stack<>();
        announce();
    }

    /** Copy the contents of BOARD into me. */
    void copy(Board board) {
        Square[][] copy = new Square[size()][size()];
        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.size(); j++) {
                Square s = board.state[i][j];
                copy[i][j] = Square.square(s.getSide(), s.getSpots());
            }
        }
        history.push(copy);
    }

    /** Copy the contents of BOARD into me, without modifying my undo
     *  history. Assumes BOARD and I have the same size. */
    private void internalCopy(Board board) {
        assert size() == board.size();
        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.size(); j++) {
                Square s = board.state[i][j];
                state[i][j] = Square.square(s.getSide(), s.getSpots());
            }
        }
    }

    /** Return the number of rows and of columns of THIS. */
    int size() {
        return state.length;
    }

    /** Returns the contents of the square at row R, column C
     *  1 <= R, C <= size (). */
    Square get(int r, int c) {
        return get(sqNum(r, c));
    }

    /** Returns the contents of square #N, numbering squares by rows, with
     *  squares in row 1 number 0 - size()-1, in row 2 numbered
     *  size() - 2*size() - 1, etc. */
    Square get(int n) {
        return state[n / size()][n % size()];
    }

    /** Returns the total number of spots on the board. */
    int numPieces() {
        int total = 0;
        for (Square[] row : state) {
            for (Square s : row) {
                total += s.getSpots();
            }
        }
        return total;
    }

    /** Returns the Side of the player who would be next to move.  If the
     *  game is won, this will return the loser (assuming legal position). */
    Side whoseMove() {
        return ((numPieces() + size()) & 1) == 0 ? RED : BLUE;
    }

    /** Return true iff row R and column C denotes a valid square. */
    final boolean exists(int r, int c) {
        return 1 <= r && r <= size() && 1 <= c && c <= size();
    }

    /** Return true iff S is a valid square number. */
    final boolean exists(int s) {
        int N = size();
        return 0 <= s && s < N * N;
    }

    /** Return the row number for square #N. */
    final int row(int n) {
        return n / size() + 1;
    }

    /** Return the column number for square #N. */
    final int col(int n) {
        return n % size() + 1;
    }

    /** Return the square number of row R, column C. */
    final int sqNum(int r, int c) {
        return (c - 1) + (r - 1) * size();
    }

    /** Return a string denoting move (ROW, COL)N. */
    String moveString(int row, int col) {
        return String.format("%d %d", row, col);
    }

    /** Return a string denoting move N. */
    String moveString(int n) {
        return String.format("%d %d", row(n), col(n));
    }

    /** Returns true iff it would currently be legal for PLAYER to add a spot
        to square at row R, column C. */
    boolean isLegal(Side player, int r, int c) {
        return r <= size() && 1 <= r && c <= size()
                && 1 <= c && isLegal(player, sqNum(r, c));
    }

    /** Returns true iff it would currently be legal for PLAYER to add a spot
     *  to square #N. */
    boolean isLegal(Side player, int n) {
        return player.playableSquare(get(n).getSide()) && isLegal(player);
    }

    /** Returns true iff PLAYER is allowed to move at this point. */
    boolean isLegal(Side player) {
        return player == whoseMove() && getWinner() == null;
    }

    /** Returns the winner of the current position, if the game is over,
     *  and otherwise null. */
    final Side getWinner() {
        Side side = state[0][0].getSide();
        if (side == WHITE) {
            return null;
        }
        for (Square[] row : state) {
            for (Square s : row) {
                if (s.getSide() !=  side) {
                    return null;
                }
            }
        }
        return side;
    }

    /** Return the number of squares of given SIDE. */
    int numOfSide(Side side) {
        int total = 0;
        for (Square[] row : state) {
            for (Square s : row) {
                if (s.getSide() ==  side) {
                    total += 1;
                }
            }
        }
        return total;
    }

    /** Add a spot from PLAYER at row R, column C.  Assumes
     *  isLegal(PLAYER, R, C). */
    void addSpot(Side player, int r, int c) {
        addSpot(player, sqNum(r, c));
    }

    /** Add a spot from PLAYER at square #N.  Assumes isLegal(PLAYER, N). */
    void addSpot(Side player, int n) {
        markUndo();
        simpleAdd(player, n, 1);
        if (get(n).getSpots() > getNeighbors(n).size()) {
            jump(n);
        }
    }


    /** Set the square at row R, column C to NUM spots (0 <= NUM), and give
     *  it color PLAYER if NUM > 0 (otherwise, white). */
    void set(int r, int c, int num, Side player) {
        internalSet(r, c, num, player);
        announce();
    }

    /** Set the square at row R, column C to NUM spots (0 <= NUM), and give
     *  it color PLAYER if NUM > 0 (otherwise, white).  Does not announce
     *  changes. */
    private void internalSet(int r, int c, int num, Side player) {
        internalSet(sqNum(r, c), num, player);
    }

    /** Set the square #N to NUM spots (0 <= NUM), and give it color PLAYER
     *  if NUM > 0 (otherwise, white). Does not announce changes. */
    private void internalSet(int n, int num, Side player) {
        if (num == 0) {
            state[n / size()][n % size()] = Square.square(WHITE, 0);
        } else {
            state[n / size()][n % size()] = Square.square(player, num);
        }

        if (getWinner() != null) {
            return;
        }
    }


    /** Undo the effects of one move (that is, one addSpot command).  One
     *  can only undo back to the last point at which the undo history
     *  was cleared, or the construction of this Board. */
    void undo() {
        if (history.isEmpty()) {
            return;
        } else {
            state = history.pop();
        }

    }

    /** Record the beginning of a move in the undo history. */
    private void markUndo() {
        copy(this);
    }

    /** Add DELTASPOTS spots of side PLAYER to row R, column C,
     *  updating counts of numbers of squares of each color. */
    private void simpleAdd(Side player, int r, int c, int deltaSpots) {
        internalSet(r, c, deltaSpots + get(r, c).getSpots(), player);
    }

    /** Add DELTASPOTS spots of color PLAYER to square #N,
     *  updating counts of numbers of squares of each color. */
    private void simpleAdd(Side player, int n, int deltaSpots) {
        internalSet(n, deltaSpots + get(n).getSpots(), player);
    }

    /** Used in jump to keep track of squares needing processing.  Allocated
     *  here to cut down on allocations. */
    private final ArrayDeque<Integer> _workQueue = new ArrayDeque<>();

    /** Do all jumping on this board, assuming that initially, S is the only
     *  square that might be over-full. */
    private void jump(int S) {
        Side color = whoseMove().opposite();
        _workQueue.add(S);
        while (_workQueue.peek() != null && getWinner() == null) {
            S = _workQueue.poll();
            int r = S / size();
            int c = S % size();
            Square s = get(r + 1, c + 1);
            if (s.getSpots() > getNeighbors(S).size()) {
                internalSet(S, 1, color);
                for (int pos : getNeighbors(S)) {
                    simpleAdd(color, pos, 1);
                    if (getWinner() != null) {
                        break;
                    }
                    _workQueue.add(pos);
                }
            }
            if (getWinner() != null) {
                break;
            }
        }
    }

    /** Gets neighbors.
     * @param S Square number
     * @return ArrayList of neighbors
     * */
    private ArrayList<Integer> getNeighbors(int S) {
        int r = S / size();
        int c = S % size();
        ArrayList<Integer> n = new ArrayList<Integer>();
        if (r - 1 >= 0) {
            n.add(sqNum(r, c + 1));
        }
        if (r + 1 < size()) {
            n.add(sqNum(r + 2, c + 1));
        }
        if (c - 1 >= 0) {
            n.add(sqNum(r + 1, c));
        }
        if (c + 1 < size()) {
            n.add(sqNum(r + 1, c + 2));
        }
        return n;
    }

    /** Returns my dumped representation. */
    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("===\n");
        for (int i = 0; i < size(); i++) {
            out.format("    ");
            for (int j = 0; j < size(); j++) {
                Square s = state[i][j];
                out.format("%d", s.getSpots());
                if (s.getSide() == RED) {
                    out.format("r");
                } else if (s.getSide() == BLUE) {
                    out.format("b");
                } else {
                    out.format("-");
                }
                if (j != size() - 1) {
                    out.format(" ");
                }

            }
            out.format("\n");
        }
        out.format("===\n");
        if (getWinner() != null) {
            if (getWinner() == RED) {
                out.format("*Red wins.");
            } else {
                out.format("*Blue wins.");
            }
        }
        return out.toString();
    }

    /** Returns an external rendition of me, suitable for human-readable
     *  textual display, with row and column numbers.  This is distinct
     *  from the dumped representation (returned by toString). */
    public String toDisplayString() {
        String[] lines = toString().trim().split("\\R");
        Formatter out = new Formatter();
        for (int i = 1; i + 1 < lines.length; i += 1) {
            out.format("%2d %s%n", i, lines[i].trim());
        }
        out.format("  ");
        for (int i = 1; i <= size(); i += 1) {
            out.format("%3d", i);
        }
        return out.toString();
    }

    /** Returns the number of neighbors of the square at row R, column C. */
    int neighbors(int r, int c) {
        int size = size();
        int n;
        n = 0;
        if (r > 1) {
            n += 1;
        }
        if (c > 1) {
            n += 1;
        }
        if (r < size) {
            n += 1;
        }
        if (c < size) {
            n += 1;
        }
        return n;
    }

    /** Returns the number of neighbors of square #N. */
    int neighbors(int n) {
        return neighbors(row(n), col(n));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Board)) {
            return false;
        } else {
            Board B = (Board) obj;
            return this == obj;
        }
    }

    @Override
    public int hashCode() {
        return numPieces();
    }

    /** Set my notifier to NOTIFY. */
    public void setNotifier(Consumer<Board> notify) {
        _notifier = notify;
        announce();
    }

    /** Take any action that has been set for a change in my state. */
    private void announce() {
        _notifier.accept(this);
    }

    /** A notifier that does nothing. */
    private static final Consumer<Board> NOP = (s) -> { };

    /** A read-only version of this Board. */
    private ConstantBoard _readonlyBoard;

    /** Use _notifier.accept(B) to announce changes to this board. */
    private Consumer<Board> _notifier;

}
