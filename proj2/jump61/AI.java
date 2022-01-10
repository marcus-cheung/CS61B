package jump61;

import java.util.ArrayList;
import java.util.Random;

import static jump61.Side.*;

/** An automated Player.
 *  @author P. N. Hilfinger
 */
class AI extends Player {

    /** A new player of GAME initially COLOR that chooses moves automatically.
     *  SEED provides a random-number seed used for choosing moves.
     */
    AI(Game game, Side color, long seed) {
        super(game, color);
        _random = new Random(seed);
    }

    @Override
    String getMove() {
        Board board = getGame().getBoard();

        assert getSide() == board.whoseMove();
        int choice = searchForMove();
        getGame().reportMove(board.row(choice), board.col(choice));
        return String.format("%d %d", board.row(choice), board.col(choice));
    }

    /** Return a move after searching the game tree to DEPTH>0 moves
     *  from the current position. Assumes the game is not over. */
    private int searchForMove() {
        Board work = new Board(getBoard());

        assert getSide() == work.whoseMove();
        _foundMove = -1;
        if (getSide() == RED) {
            minMax(work, 4, true, 1,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
        } else {
            minMax(work, 4, true, -1,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
        return _foundMove;
    }


    /** Find a move from position BOARD and return its value, recording
     *  the move found in _foundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _foundMove. If the game is over
     *  on BOARD, does not set _foundMove. */
    private int minMax(Board board, int depth, boolean saveMove,
                       int sense, int alpha, int beta) {
        if ((staticEval(board, 0) == board.size() * board.size())
                || depth == 0) {
            return staticEval(board, 0);
        }
        int bestMove = -1;
        int best = -1 * sense * Integer.MAX_VALUE;
        for (int m : legalMoves(board)) {
            Board next = new Board(board);
            next.addSpot(next.whoseMove(), m);
            int response = minMax(next, depth - 1, false,
                    -1 * sense, alpha, beta);
            if (response * sense > best * sense) {
                best = response;
                bestMove = m;
                if (sense == 1) {
                    alpha = Math.max(alpha, best);
                } else {
                    beta  = Math.min(beta, best);
                }
                if (alpha >= beta && !saveMove) {
                    return best;
                }
            } else if (best == response) {
                bestMove = m;
            }
        }
        if (saveMove) {
            _foundMove = bestMove;
        }
        return best;
    }

    /** Return a heuristic estimate of the value of board position B.
     *  Use WINNINGVALUE to indicate a win for Red and -WINNINGVALUE to
     *  indicate a win for Blue. */
    private int staticEval(Board b, int winningValue) {
        int red = b.numOfSide(RED);
        int blue = b.numOfSide(BLUE);
        if (getSide() == RED) {
            return red;
        } else {
            return -blue;
        }
    }

    /** Get legal moves.
     * @param b Board to check
     * @return Arraylist of legal moves
     * */
    private ArrayList<Integer> legalMoves(Board b) {
        ArrayList<Integer> moves = new ArrayList<>();
        for (int i = 1; i <= b.size(); i++) {
            for (int j = 1; j <= b.size(); j++) {
                if (b.isLegal(getSide(), i, j)) {
                    moves.add(b.sqNum(i, j));
                }
            }
        }
        return moves;
    }

    /** A random-number generator used for move selection. */
    private Random _random;

    /** Used to convey moves discovered by minMax. */
    private int _foundMove;
}
