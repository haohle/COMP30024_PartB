/* Hao Le - leh2
 * Sam Chung - chungs1
 * 
 * Board Class - Handles the implementation of the gameboard
 * Last Modified: 07/04/17
 *
 */

package aiproj.slider;

import java.awt.Point;
import java.util.ArrayList;

public class Board {

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
        } else {
            // piece is +

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

//       for (i = 0; i < boardSize; i++) {

        y = 0;
        for (i = boardSize-1; i != -1; i--) {
            x = 0;
//            System.out.println("TEST");
//            System.out.println(raw[i]);
//            System.out.println("TEST");

            for (j = 0; j < raw[i].length(); j += 2) {
//                System.out.print(x + "," + y + " ");
                if (raw[i].charAt(j) == BLOCK) {
                    // implement block cell
                    board[x][y] = new Cell(x, y, new Piece(raw[i].charAt(j)));
                } else if (raw[i].charAt(j) == EMPTY) {
                    // implement empty cell
                    board[x][y] = new Cell(x, y, new Piece(raw[i].charAt(j)));
//                    System.out.print(raw[i].charAt(j));
                } else if (raw[i].charAt(j) == VERT || raw[i].charAt(j) == HORI) {
                    // implement new piece
                    board[x][y] = new Cell(x, y, new Piece(raw[i].charAt(j)));

                    // implement piece location vectors
                    if (raw[i].charAt(j) == VERT) {
//                        System.out.print("V ");
                        playerVLocations.add(new Point(x, y));
                    } else {
//                        System.out.print("H ");
                        playerHLocations.add(new Point(x, y));
                    }
                } else {
                    // bad input
                    System.out.println("Invalid input - terminating");
                    System.exit(0);
                }
                x++;
//                System.out.print("\n");
            }

//
//            System.out.println("Verify");
//            for (int k = 0; k < boardSize; k++) {
//                System.out.print(k + "," + y);
//                System.out.print(board[k][y].getPieceTypeChar() + " ");
//            }
//            System.out.print("\n");
            y++;
        }
    }
}
