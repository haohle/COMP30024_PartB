/* Hao Le - leh2
 * Sam Chung - chungs1
 *
 * Player Class - Not used currently for project A
 *                will probably be needed for project B
 * Last Modified: 07/04/17
 *
 */

package aiproj.slider;
import java.awt.Point;

public class Player implements SliderPlayer {
    private int d;
    private String b[];
    private char p;

    private Board gameBoard;

    public void init(int dimension, String board, char player) {
        this.d = dimension;
        this.b = board.split("\n");
        this.p = player;

        this.gameBoard = new Board(dimension, this.b);

        /**
         * testing purposes only, this is what the original board looks like
         * even the referee has this, however, when the board is rendered, ref flips it
         */
//        for (int j = 0; j < dimension; j++) {
//            for (int i = 0; i < dimension; i++) {
//                System.out.print(i + "," + j + " " + test[i][j].getPieceTypeChar() + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("FLIPPED");
//        // flipped version
//        for (int j = dimension-1; j >= 0; j--) {
//            for (int i = 0; i < dimension; i++) {
//                System.out.print(i + "," + j + " " + test[i][j].getPieceTypeChar() + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println("DONE");
    }

    public Move move() {
        Move move = null;

        // hard coding of moves
        if (this.p == 'H') {
            if (this.gameBoard.getBoard()[0][1].getPieceTypeChar() == 'H') {
                move = new Move(0, 1, Move.Direction.RIGHT);
            } else {
                move = new Move(0, 2, Move.Direction.RIGHT);
            }
        }
        if (this.p == 'V') {
            if (this.gameBoard.getBoard()[4][0].getPieceTypeChar() == 'V') {
                move = new Move(4, 0, Move.Direction.UP);
            } else {
                move = new Move(3, 0, Move.Direction.UP);
            }
        }

        this.update(move);
        return move;
    }

    /** 
     * if we are here, then we can assume that the previous move is already valid
     * and has already taken place, ignore all move checking and just update the internal board
     */
    public void update(Move move) {
        // no move was made, don't update board
        if (move == null) {
            return;
        }

        // cell that is being moved away from
        Cell cell = this.gameBoard.getBoard()[move.i][move.j];
        char tmpCellChar = cell.getPieceTypeChar();

        // cell that is being moved to
        int to_i = move.i, to_j = move.j;
        switch(move.d) {
            case UP:    to_j++; break;
            case DOWN:  to_j--; break;
            case RIGHT: to_i++; break;
            case LEFT:  to_i--; break;
        }

        // moving to a location which is off the board?
        if (cell.getPieceTypeChar() == 'H' && to_i == this.d) {
            this.gameBoard.getBoard()[move.i][move.j].setPieceTypeChar('+');

            // remove point from ArrayList - used to keep track of pieces
            for (Point point : this.gameBoard.getPlayerHLocations()) {
                // find the point which is the one that's being moved off board
                if (point.getX() == move.i && point.getY() == move.j) {
                    this.gameBoard.getPlayerHLocations().remove(point);
                }
            }

            return;
        }

        if (cell.getPieceTypeChar() == 'V' && to_j == this.d) {
            this.gameBoard.getBoard()[move.i][move.j].setPieceTypeChar('+');
            // removes point from ArrayList - used to keep track of pieces
            for (Point point : this.gameBoard.getPlayerHLocations()) {
                // find the point which is the one that's being moved off board
                if (point.getX() == move.i && point.getY() == move.j) {
                    this.gameBoard.getPlayerHLocations().remove(point);
                }
            }

            return;
        }

        // make move (not off board)
        this.gameBoard.updateBoard(move.i, move.j, '+');
        this.gameBoard.updateBoard(to_i, to_j, tmpCellChar);



//        Used for testing
//        Cell[][] test = this.gameBoard.getBoard();
//        for (int j = this.d-1; j >= 0; j--) {
//            for (int i = 0; i < this.d; i++) {
//                System.out.print(test[i][j].getPieceTypeChar() + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.print("DONE\n");


        return;
    }

}
