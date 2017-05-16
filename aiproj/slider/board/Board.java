/* Hao Le - leh2
 * Sam Chung - chungs1
 * 
 * Board Class - Handles the implementation of the gameboard
 * Last Modified: 07/04/17
 *
 */

package aiproj.slider.board;

import aiproj.slider.Move;

import java.lang.Enum;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

public class Board implements Cloneable {

    private static final char BLOCK = 'B';
    private static final char EMPTY = '+';
    private static final char VERT = 'V';
    private static final char HORI = 'H';

    private static final boolean BLOCKED = true;

    private Cell[][] board;
    private Integer boardSize = 0;
    private ArrayList<Point> playerHLocations;
    private ArrayList<Point> playerVLocations;

    /**
     * Returns an arraylist of player V piece locations
     */
    public ArrayList<Point> getPlayerVLocations() {
        return playerVLocations;
    }

    /**
     * Returns an arraylist of player H piece locations
     */
    public ArrayList<Point> getPlayerHLocations() {
        return playerHLocations;
    }

    /**
     * Returns the gameboard
     */
    public Cell[][] getBoard() {
        return board;
    }

    public void updateBoard(int x, int y, char piece) {
        this.board[x][y] = new Cell(x, y, new Piece(piece));

        if (piece == 'H') {
            this.playerHLocations.add(new Point(x, y));
        } else if (piece == 'V') {
            this.playerVLocations.add(new Point(x, y));
        }
    }

    /**
     * Returns the gameboard's size
     */
    public Integer getBoardSize() {
        return boardSize;
    }

    /**
     * The game board
     * @param boardSize How big the board is (board is square)
     * @param raw The input taken of the current state of the board
     */
    public Board(int boardSize, String[] raw) {
        int i, j, x, y;
        this.boardSize = boardSize;
        board = new Cell[boardSize][boardSize];
        playerHLocations = new ArrayList<Point>(boardSize);
        playerVLocations = new ArrayList<Point>(boardSize);


        y = 0;
        for (i = boardSize-1; i != -1; i--) {
            x = 0;

            for (j = 0; j < raw[i].length(); j += 2) {
                if (raw[i].charAt(j) == BLOCK) {
                    // implement block cell
                    board[x][y] = new Cell(x, y, new Piece(raw[i].charAt(j)));
                } else if (raw[i].charAt(j) == EMPTY) {
                    // implement empty cell
                    board[x][y] = new Cell(x, y, new Piece(raw[i].charAt(j)));
                } else if (raw[i].charAt(j) == VERT || raw[i].charAt(j) == HORI) {
                    // implement new piece
                    board[x][y] = new Cell(x, y, new Piece(raw[i].charAt(j)));

                    // implement piece location vectors
                    if (raw[i].charAt(j) == VERT) {
                        playerVLocations.add(new Point(x, y));
                    } else {
                        playerHLocations.add(new Point(x, y));
                    }
                } else {
                    // bad input
                    System.out.println("Invalid input - terminating");
                    System.exit(0);
                }
                x++;
            }
            y++;
        }
    }

    /**
     * Updates the board state by applying the provided move
     * @param b, Board state
     * @param move, Move to be applied to the board
     */
    public void update(Board b, Move move) {
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
        if (to_i == b.boardSize) {
            b.getBoard()[move.i][move.j].setPieceTypeChar('+');

            /* remove point from ArrayList - used to keep track of pieces */
            for (Iterator<Point> it = b.getPlayerHLocations().iterator(); it.hasNext();) {
                Point point = it.next();
                /* find the point which is the one that's being moved off board */
                if (point.getX() == move.i && point.getY() == move.j) {
                    it.remove();
                }

            }

            /* update the board and make it free at this cell */
            // TO DO: might not even need this
            b.getBoard()[move.i][move.j].setBlock(false);

            return;
        }

        if (to_j == b.boardSize) {
            b.getBoard()[move.i][move.j].setPieceTypeChar('+');

            /* removes point from ArrayList - used to keep track of pieces */
            for (Iterator<Point> it = b.getPlayerVLocations().iterator(); it.hasNext();) {
                Point point = it.next();
                /* find the point which is the one that's being moved off board */
                if (point.getX() == move.i && point.getY() == move.j) {
                    it.remove();
                }
            }

            /* update the board and make it free at this cell */
            // TO DO: might not even need this
            b.getBoard()[move.i][move.j].setBlock(false);

            return;
        }

        /* cell that is being moved away from */
        Cell cell = b.getBoard()[move.i][move.j];
        char tmpCellChar = cell.getPieceTypeChar();

        /* make move (not off board) - update board */
        b.updateBoard(move.i, move.j, '+');
        b.updateBoard(to_i, to_j, tmpCellChar);

        /* used to update the players memory of where pieces are currently at */
        if (tmpCellChar == 'H') {
            for (Iterator<Point> it = b.getPlayerHLocations().iterator(); it.hasNext();) {
                Point point = it.next();
                /* find the point which is the one that's being moved off board */
                if (point.getX() == move.i && point.getY() == move.j) {
                    it.remove();
                }
            }
        }
        if (tmpCellChar == 'V') {
            for (Iterator<Point> it = b.getPlayerVLocations().iterator(); it.hasNext();) {
                Point point = it.next();
                /* find the point which is the one that's being moved off board */
                if (point.getX() == move.i && point.getY() == move.j) {
                    it.remove();
                }
            }
        }
    }

    /**
     * Outputs the current board state to a string
     * This is primarily used for Monte Carlo when a copy of the board is needed
     */
    public String toString(){
        StringBuilder s = new StringBuilder(2 * boardSize * boardSize);
        for (int j = boardSize-1; j >= 0; j--) {
            s.append(board[0][j].getPieceTypeChar());
            for (int i = 1; i < boardSize; i++) {
                s.append(' ');
                s.append(board[i][j].getPieceTypeChar());
            }
            s.append('\n');
        }
        return s.toString();
    }


    /**
     * Outputs the board provided in the param
     * @param b, Board which is to be printed
     */
    public void printB(Board b) {
        System.out.print("H: ");
        System.out.println(b.getPlayerHLocations());
        System.out.print("V: ");
        System.out.println(b.getPlayerVLocations());

        Cell[][] test = b.getBoard();
        for (int j = b.boardSize - 1; j >= 0; j--) {
            for (int i = 0; i < b.boardSize; i++) {
                System.out.print(test[i][j].getPieceTypeChar() + " ");
            }
            System.out.print("\n");
        }
    }
}
