package jump61;

import ucb.gui2.Pad;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import java.util.concurrent.ArrayBlockingQueue;
/** A GUI component that displays a Jump61 board, and converts mouse clicks
 *  on that board to commands that are sent to the current Game.
 *  @author Marcus Cheung
 */
class BoardWidget extends Pad {

    /** Length of the side of one square in pixels. */
    private static final int SQUARE_SIZE = 50;
    /** Width and height of a spot. */
    private static final int SPOT_DIM = 8;
    /** Minimum separation of center of a spot from a side of a square. */
    private static final int SPOT_MARGIN = 10;
    /** Width of the bars separating squares in pixels. */
    private static final int SEPARATOR_SIZE = 3;
    /** Width of square plus one separator. */
    private static final int SQUARE_SEP = SQUARE_SIZE + SEPARATOR_SIZE;

    /** Colors of various parts of the displayed board. */
    private static final Color
        NEUTRAL = Color.WHITE,
        SEPARATOR_COLOR = Color.BLACK,
        SPOT_COLOR = Color.BLACK,
        RED_TINT = new Color(255, 200, 200),
        BLUE_TINT = new Color(200, 200, 255);

    /** A new BoardWidget that monitors and displays a game Board, and
     *  converts mouse clicks to commands to COMMANDQUEUE. */
    BoardWidget(ArrayBlockingQueue<String> commandQueue) {
        _commandQueue = commandQueue;
        _side = 6 * SQUARE_SEP + SEPARATOR_SIZE;
        setMouseHandler("click", this::doClick);
    }

    /* .update and .paintComponent are synchronized because they are called
     *  by three different threads (the main thread, the thread that
     *  responds to events, and the display thread).  We don't want the
     *  saved copy of our Board to change while it is being displayed. */

    /** Update my display to show BOARD.  Here, we save a copy of
     *  BOARD (so that we can deal with changes to it only when we are ready
     *  for them), and recompute the size of the displayed board. */
    synchronized void update(Board board) {
        if (board.equals(_board)) {
            return;
        }
        if (_board != null && _board.size() != board.size()) {
            invalidate();
        }
        _board = new Board(board);
        _side = _board.size() * SQUARE_SEP + SEPARATOR_SIZE;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(_side, _side);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(_side, _side);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(_side, _side);
    }

    @Override
    public synchronized void paintComponent(Graphics2D g) {
        if (_board == null) {
            return;
        }
        g.setColor(SEPARATOR_COLOR);
        int size = _board.size() * SQUARE_SEP + SEPARATOR_SIZE;
        g.fillRect(0, 0, size, size);
        for (int r = 1; r <= _board.size(); r++) {
            for (int c = 1; c <= _board.size(); c++) {
                Side color = _board.get(r, c).getSide();
                if (color == Side.RED) {
                    g.setColor(RED_TINT);
                } else if (color == Side.BLUE){
                    g.setColor(BLUE_TINT);
                } else {
                    g.setColor(NEUTRAL);
                }
                int x = SQUARE_SEP * (r - 1) + SEPARATOR_SIZE;
                int y = SQUARE_SEP * (c - 1) + SEPARATOR_SIZE;
                g.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
                displaySpots(g, r, c);
            }
        }
    }

    /** Color and display the spots on the square at row R and column C
     *  on G.  (Used by paintComponent). */
    private void displaySpots(Graphics2D g, int r, int c) {
        g.setColor(SPOT_COLOR);
        int numSpots = _board.get(r, c).getSpots();
        int x = SQUARE_SEP * (r - 1) + SEPARATOR_SIZE;
        int y = SQUARE_SEP * (c - 1) + SEPARATOR_SIZE;
        if (numSpots == 1) {
            spot(g, x + SQUARE_SIZE / 2 + SEPARATOR_SIZE + SPOT_DIM / 2
                    , y + SQUARE_SIZE / 2 + SEPARATOR_SIZE + SPOT_DIM / 2);
        } else if (numSpots == 2) {
            int margin = SPOT_MARGIN + SPOT_DIM / 2;
            spot(g, x + margin, y + SQUARE_SIZE - margin);
            spot(g, x + SQUARE_SIZE - margin, y + margin);
        } else if (numSpots == 3) {
            spot(g, x + SQUARE_SIZE / 2 + SEPARATOR_SIZE + SPOT_DIM / 2
                    , y + SQUARE_SIZE / 2 + SEPARATOR_SIZE + SPOT_DIM / 2);
            int margin = SPOT_MARGIN + SPOT_DIM / 2;
            spot(g, x + margin, y + SQUARE_SIZE - margin);
            spot(g, x + SQUARE_SIZE - margin, y + margin);
        } else if (numSpots == 4) {
            int margin = SPOT_MARGIN + SPOT_DIM / 2;
            spot(g, x + margin, y + SQUARE_SIZE - margin);
            spot(g, x + SQUARE_SIZE - margin, y + margin);
            spot(g, x + SQUARE_SIZE - margin, y + SQUARE_SIZE - margin);
            spot(g, x + margin, y + margin);
        } else {
            spot(g, x + SQUARE_SIZE / 2 + SEPARATOR_SIZE + SPOT_DIM / 2
                    , y + SQUARE_SIZE / 2 + SEPARATOR_SIZE + SPOT_DIM / 2);
            int margin = SPOT_MARGIN + SPOT_DIM / 2;
            spot(g, x + margin, y + SQUARE_SIZE - margin);
            spot(g, x + SQUARE_SIZE - margin, y + margin);
            spot(g, x + SQUARE_SIZE - margin, y + SQUARE_SIZE - margin);
            spot(g, x + margin, y + margin);
        }
    }

    /** Draw one spot centered at position (X, Y) on G. */
    private void spot(Graphics2D g, int x, int y) {
        g.setColor(SPOT_COLOR);
        g.fillOval(x - SPOT_DIM / 2, y - SPOT_DIM / 2, SPOT_DIM, SPOT_DIM);
    }

    /** Respond to the mouse click depicted by EVENT. */
    public void doClick(String dummy, MouseEvent event) {
        // x and y coordinates relative to the upper-left corner of the
        // upper-left square (increasing y is down).
        int x = event.getX() - SEPARATOR_SIZE,
            y = event.getY() - SEPARATOR_SIZE;
        // FIXME:
        // REPLACE THE FOLLOWING WITH COMPUTATION OF r AND c FROM x and y,
        // AND SEND THE APPROPRIATE COMMAND TO OUR GAME, IF THE EVENT
        // OCCURS AT A VALID POSITION.  OTHERWISE, DOES NOTHING.
        int r = x / SQUARE_SIZE;
        int c = y / SQUARE_SIZE;
        if (_board.isLegal(_board.whoseMove(), r, c)) {
            _commandQueue.offer(String.format("%d %d", r, c));
        }
    }

    /** The Board I am displaying. */
    private Board _board;
    /** Dimension in pixels of one side of the board. */
    private int _side;
    /** Destination for commands derived from mouse clicks. */
    private ArrayBlockingQueue<String> _commandQueue;
}
