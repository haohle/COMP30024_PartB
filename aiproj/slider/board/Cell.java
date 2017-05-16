/* Hao Le - leh2
 * Sam Chung - chungs1
 * 
 * Cell Class - Handles each individual cell on the board
 * Last Modified: 07/04/17
 *
 */

package aiproj.slider.board;

public class Cell {
    private boolean isBlocked;
    private Piece pieceType = null;
    private double xPos;
    private double yPos;

    /**
     * Getter for a cell's piece type
     */
    public char getPieceTypeChar() {
        if (this.pieceType != null) {
            return pieceType.toChar();
        }

        // shouldn't happen
        return 'x';
    }

    public void setPieceTypeChar(char pieceTypeChar) {
        this.pieceType.setChar(pieceTypeChar);
    }

    public Piece getPieceType() {
        if (this.pieceType != null) {
            return this.pieceType;
        }

        return null;
    }

    public void setPieceType(Piece pieceType) {
        this.pieceType = pieceType;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlock(boolean block) {
        this.isBlocked = block;
    }

    /**
     * Cell used for player pieces
     * @param x X Coordinate of the cell
     * @param y Y Coordinate of the cell
     * @param value The piece type and who it belongs to
     */
    public Cell(double x, double y, Piece value) {
        this.pieceType = value;
        this.xPos = x;
        this.yPos = y;

        if (value.toChar() == '+') {
            this.isBlocked = false;
        } else {
            this.isBlocked = true;
        }
    }

    /**
     * Get the number of surrounding possible moves
     * @param piece The piece that is getting its surrounding pieces tested
     * @param gameBoard The gameboard in which the cell is contained within
     */
    public int getSurrounding(char piece, Board gameBoard) {
        int num_moves = 0;

        while (piece == 'H') {
            // checks above, avoids top most row
            if (this.yPos != gameBoard.getBoardSize() - 1  && !gameBoard.getBoard()[(int) this.xPos][(int) this.yPos + 1].isBlocked()) {
                num_moves += 1;
            }

            // checks right and finish line
            if (this.xPos == gameBoard.getBoardSize() - 1) {
                num_moves += 1;
            } else if (!gameBoard.getBoard()[(int) this.xPos + 1][(int) this.yPos].isBlocked()) {
                num_moves += 1;
            }

            // checks below, avoids bottom most row
            if (this.yPos != 0
                    && !gameBoard.getBoard()[(int) this.xPos][(int) this.yPos - 1].isBlocked()) {
                num_moves += 1;
            }

            return num_moves;
        }

        while (piece == 'V') {
            // checks left, avoids far left column
            if (this.xPos != 0 && !gameBoard.getBoard()[(int) this.xPos - 1][(int) this.yPos].isBlocked()) {
                num_moves += 1;
            }

            // checks above and finish line
            if (this.yPos == gameBoard.getBoardSize() - 1) {
                num_moves += 1;
            } else if (!gameBoard.getBoard()[(int) this.xPos][(int) this.yPos + 1].isBlocked()) {
                num_moves += 1;
            }

            // checks right, avoids far right column
            if (this.xPos != gameBoard.getBoardSize() - 1
                    && !gameBoard.getBoard()[(int) this.xPos + 1][(int) this.yPos].isBlocked()) {
                num_moves += 1;
            }
            return num_moves;
        }

        // if here, it means there are no legal moves
        return num_moves;
    }
}
