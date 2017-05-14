package aiproj.slider.board;

/* Hao Le - leh2
 * Sam Chung - chungs1
 * 
 * Piece Class - Contains information regarding the player pieces
 * Last Modified: 07/04/17
 *
 */

public class Piece {
    private char pieceType;

    public char toChar() {
        return pieceType;
    }

    public void setChar(char pieceType) { this.pieceType = pieceType; }

    public Piece(char pieceType) {
        this.pieceType = pieceType;
    }
}
