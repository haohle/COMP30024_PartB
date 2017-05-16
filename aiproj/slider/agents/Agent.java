package aiproj.slider.agents;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import aiproj.slider.Move;
import aiproj.slider.SliderPlayer;
import aiproj.slider.board.Board;
import aiproj.slider.board.Cell;

/**
 * Created by hao on 15/5/17.
 */
public class Agent implements SliderPlayer {

    /* AgentAlphaBeta */
    protected char player;
    /* Opponent */
    protected char opponent;

    /* AgentAlphaBeta's internal gameboard */
    protected int dimension;
    protected String rawBoard[];
    protected Board gameBoard;

    /* List of all possible makes the player can make in current state */
    protected List<Move> possMoves;
    protected List<Move> oppMoves;
    protected HashMap hm = new HashMap();

    public void init(int d, String b, char p) {
        this.dimension = d;
        this.player = p;
        this.rawBoard = b.split("\n"); // process the board for it to be read in

        /* reads in and generates player's internal board */
        this.gameBoard = new Board(this.dimension, this.rawBoard);

        /* identifies who te opposing player is */
        if (player == 'V') {
            this.opponent = 'H';
        } else {
            this.opponent = 'V';
        }
    }

    public Move move() {

        return null;
    }

    public void update(Move move) {
        /* no move was made, don't update board */
        if (move == null) {
            return;
        }

        /* cell that is being moved to */
        int to_i = move.i, to_j = move.j;
        switch (move.d) {
            case UP:
                to_j++;
                break;
            case DOWN:
                to_j--;
                break;
            case RIGHT:
                to_i++;
                break;
            case LEFT:
                to_i--;
                break;
        }

        /* moving to a location which is off the board? */
        if (to_i == this.dimension) {
            this.gameBoard.getBoard()[move.i][move.j].setPieceTypeChar('+');

            /* remove point from ArrayList - used to keep track of pieces */
            for (Iterator<Point> it = this.gameBoard.getPlayerHLocations().iterator(); it.hasNext();) {
                Point point = it.next();
                /* find the point which is the one that's being moved off board */
                if (point.getX() == move.i && point.getY() == move.j) {
                    it.remove();
                }

            }

            /* update the board and make it free at this cell */
            // TO DO: might not even need this
            this.gameBoard.getBoard()[move.i][move.j].setBlock(false);

            return;
        }

        if (to_j == this.dimension) {
//            System.out.println("HERE " + move.i + " , " + move.j);
            this.gameBoard.getBoard()[move.i][move.j].setPieceTypeChar('+');

            /* removes point from ArrayList - used to keep track of pieces */
            for (Iterator<Point> it = this.gameBoard.getPlayerVLocations().iterator(); it.hasNext();) {
                Point point = it.next();
                /* find the point which is the one that's being moved off board */
                if (point.getX() == move.i && point.getY() == move.j) {
                    it.remove();
                }
            }

            /* update the board and make it free at this cell */
            // TO DO: might not even need this
            this.gameBoard.getBoard()[move.i][move.j].setBlock(false);

            return;
        }

        /* cell that is being moved away from */
        Cell cell = this.gameBoard.getBoard()[move.i][move.j];
        char tmpCellChar = cell.getPieceTypeChar();

        /* make move (not off board) - update board */
        this.gameBoard.updateBoard(move.i, move.j, '+');
        this.gameBoard.updateBoard(to_i, to_j, tmpCellChar);

        /* used to update the players memory of where pieces are currently at */
        if (tmpCellChar == 'H') {
            for (Iterator<Point> it = this.gameBoard.getPlayerHLocations().iterator(); it.hasNext();) {
                Point point = it.next();
                /* find the point which is the one that's being moved off board */
                if (point.getX() == move.i && point.getY() == move.j) {
                    it.remove();
                }
            }
        }
        if (tmpCellChar == 'V') {
            for (Iterator<Point> it = this.gameBoard.getPlayerVLocations().iterator(); it.hasNext();) {
                Point point = it.next();
                /* find the point which is the one that's being moved off board */
                if (point.getX() == move.i && point.getY() == move.j) {
                    it.remove();
                }
            }
        }
    }

    /**
     * Get the actual surrounding possible moves
     * @param player The player that is getting possible next moves generated
     */
    public List<Move> generateMoves(char player, Board b) {
        List<Move> nextMoves = new ArrayList<>();

        if (player == 'H') {
            for (int i = 0; i < b.getPlayerHLocations().size(); i++) {
                /* retrieve piece location(s) */
                double tmpX = b.getPlayerHLocations().get(i).getX();
                double tmpY = b.getPlayerHLocations().get(i).getY();

                /* checks above, avoids top most row */
                if (tmpY != b.getBoardSize() - 1
                        && !b.getBoard()[(int) tmpX][(int) tmpY + 1].isBlocked()) {
                    nextMoves.add(new Move((int) tmpX, (int) tmpY, Move.Direction.UP));
                }

                /* checks right and finish line */
                if (tmpX == b.getBoardSize() - 1) {
                    nextMoves.add(new Move((int) tmpX, (int) tmpY, Move.Direction.RIGHT));
                } else if (!b.getBoard()[(int) tmpX + 1][(int) tmpY].isBlocked()) {
                    nextMoves.add(new Move((int) tmpX, (int) tmpY, Move.Direction.RIGHT));
                }

                /* checks below, avoids bottom most row */
                if (tmpY != 0 && !b.getBoard()[(int) tmpX][(int) tmpY - 1].isBlocked()) {
                    nextMoves.add(new Move((int) tmpX, (int) tmpY, Move.Direction.DOWN));
                }
            }
        }

        if (player == 'V') {
            for (int i = 0; i < b.getPlayerVLocations().size(); i++) {
                /* retrieve piece location(s) */
                double tmpX = b.getPlayerVLocations().get(i).getX();
                double tmpY = b.getPlayerVLocations().get(i).getY();

                /* checks left, avoids far left column */
                if (tmpX != 0 && !b.getBoard()[(int) tmpX - 1][(int) tmpY].isBlocked()) {
                    nextMoves.add(new Move((int) tmpX, (int) tmpY, Move.Direction.LEFT));
                }

                /* checks above and finish line */
                if (tmpY == b.getBoardSize() - 1) {
                    nextMoves.add(new Move((int) tmpX, (int) tmpY, Move.Direction.UP));
                } else if (!b.getBoard()[(int) tmpX][(int) tmpY + 1].isBlocked()) {
                    nextMoves.add(new Move((int) tmpX, (int) tmpY, Move.Direction.UP));
                }

                /* checks right, avoids far right column */
                if (tmpX != b.getBoardSize() - 1
                        && !b.getBoard()[(int) tmpX + 1][(int) tmpY].isBlocked()) {
                    nextMoves.add(new Move((int) tmpX, (int) tmpY, Move.Direction.RIGHT));
                }
            }
        }

        return nextMoves;
    }

    public void printBoard() {
        System.out.print("H: ");
        System.out.println(this.gameBoard.getPlayerHLocations());
        System.out.print("V: ");
        System.out.println(this.gameBoard.getPlayerVLocations());

        Cell[][] test = this.gameBoard.getBoard();
        for (int j = this.dimension - 1; j >= 0; j--) {
            for (int i = 0; i < this.dimension; i++) {
                System.out.print(test[i][j].getPieceTypeChar() + " ");
            }
            System.out.print("\n");
        }
    }

    /**
     * Calculate the number of legal moves for player
     * @param player The player that is getting its number of moves calculated
     */
    public int numLegalMoves(char player, Board board) {
        int num_moves = 0;

        if (player == 'H') {
            for (int i = 0; i < board.getPlayerHLocations().size(); i++) {
                /* retrieve piece location(s) */
                double tmpX = board.getPlayerHLocations().get(i).getX();
                double tmpY = board.getPlayerHLocations().get(i).getY();

                Cell tmpCell = board.getBoard()[(int) tmpX][(int) tmpY];

                num_moves += tmpCell.getSurrounding(tmpCell.getPieceTypeChar(), board);
            }
        }

        if (player == 'V') {
            for (int i = 0; i < board.getPlayerVLocations().size(); i++) {
                /* retrieve piece location(s) */
                double tmpX = board.getPlayerVLocations().get(i).getX();
                double tmpY = board.getPlayerVLocations().get(i).getY();

                Cell tmpCell = board.getBoard()[(int) tmpX][(int) tmpY];

                num_moves += tmpCell.getSurrounding(tmpCell.getPieceTypeChar(), board);
            }
        }

        return num_moves;
    }

    public int numBlocks(char player, Board board) {
        int num_blocks = 0;

        if (player == 'H') {
            for (int i = 0; i < board.getPlayerVLocations().size(); i++) {
                double tmpX = board.getPlayerVLocations().get(i).getX();
                double tmpY = board.getPlayerVLocations().get(i).getY();

                if (tmpY == dimension - 1) {
                    continue;
                }

                /* check to see if the piece in front of V is blocked */
                if (board.getBoard()[(int) tmpX][(int) tmpY + 1].isBlocked()) {
                    num_blocks += 1;
                }
            }
        }

        if (player == 'V') {
            for (int i = 0; i < board.getPlayerHLocations().size(); i++) {
                double tmpX = board.getPlayerHLocations().get(i).getX();
                double tmpY = board.getPlayerHLocations().get(i).getY();

                if (tmpX == dimension - 1) {
                    continue;
                }

                /* check to see if the piece in front of H is blocked */
                if (board.getBoard()[(int) tmpX + 1][(int) tmpY].isBlocked()) {
                    num_blocks += 1;
                }
            }
        }

        return num_blocks;
    }

}
