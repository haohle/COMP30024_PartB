/* Hao Le - leh2
 * Sam Chung - chungs1
 *
 * Player Class - Not used currently for project A
 *                will probably be needed for project B
 * Last Modified: 07/04/17
 *
 */

package aiproj.slider;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Player implements SliderPlayer {
    private int d;
    private String b[];
    /*Player*/
    private char p;
    /*Opponent*/
    private char o;

    private Board gameBoard;

    private Random rng;

    private List<Move> possMoves;

    public void init(int dimension, String board, char player) {
        this.d = dimension;
        this.b = board.split("\n");
        this.p = player;
        if (player == 'V'){
            this.o = 'H';
        }
        else{
            this.o = 'V';
        }

        this.gameBoard = new Board(dimension, this.b);

        long seed = System.nanoTime();
        rng = new Random(seed);

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
        // Move move = null;
        // int depth = 1;
        // List<Move> moveList = generateMoves(this.p);
        // System.out.println(generateMoves(this.p));
        // if (moveList.size() == 0) { // no moves are possible, pass
        //     System.out.println("NO MOVES POSSIBLE");
        //     return null;
        // }

        // int i = 0;
        // int listSize = moveList.size();
        // int max = minimax(this.gameBoard, moveList.get(i), depth-1, false);
        // move = moveList.get(i);
        // reverse(moveList.get(i));
        // i++;
        // // Return highest score of moves.
        // for(;i<listSize;i++){
        //     int moveScore = minimax(this.gameBoard, moveList.get(i), depth-1, false);
        //     if(moveScore > max){
        //         max = moveScore;
        //         move = moveList.get(i);
        //         reverse(moveList.get(i));
        //     }
        // }

//        Move move = null;
        int depth = 1;
        int maxScore = 0, moveScore;
        Move bestMove = null;
        boolean first = true;

        this.possMoves = generateMoves(this.p);
//        System.out.println(possMoves);

        // no moves are possible, pass turn
        if (possMoves.size() == 0) {
            System.out.println("NO MOVES POSSIBLE");
            return null;
        }

        // if only one move is possible at this current state, no reason to evaluate it
        // just make the move
        if (possMoves.size() == 1) {
            bestMove = possMoves.get(0);
            System.out.println("About to end");

            System.out.println(bestMove.i + "," + bestMove.j);
            System.out.println(this.gameBoard.getBoard()[bestMove.i][bestMove.j].getPieceTypeChar());

            System.out.println("Move I'm going to make " + bestMove);



            System.out.println("possible moves " + possMoves);
            System.out.print("H: ");
            System.out.println(this.gameBoard.getPlayerHLocations());
            System.out.print("V: ");
            System.out.println(this.gameBoard.getPlayerVLocations());

            this.update(bestMove);
            return bestMove;
        }

        System.out.println(possMoves);
        /* if here then there is more than one possible move to make, must evaluate which is the optimal move */
        for (Move move : possMoves) {
            // get value of first move and make it the bestMove as we haven't seen anything else yet
            if (first) {
                System.out.println("FIRST");
                System.out.println("INITIAL");
                System.out.print("H: ");
                System.out.println(this.gameBoard.getPlayerHLocations());
                System.out.print("V: ");
                System.out.println(this.gameBoard.getPlayerVLocations());
                printBoard();

                maxScore = minimax(this.gameBoard, move, depth-1, false);
                System.out.println(move);
                System.out.println("MODIFIED");
                System.out.print("H: ");
                System.out.println(this.gameBoard.getPlayerHLocations());
                System.out.print("V: ");
                System.out.println(this.gameBoard.getPlayerVLocations());
                printBoard();
                System.out.println("REVERSED");
                reverse(move);
                System.out.print("H: ");
                System.out.println(this.gameBoard.getPlayerHLocations());
                System.out.print("V: ");
                System.out.println(this.gameBoard.getPlayerVLocations());
                printBoard();
                first = false;
                continue;
            }

            System.out.println("INITIAL");
            System.out.print("H: ");
            System.out.println(this.gameBoard.getPlayerHLocations());
            System.out.print("V: ");
            System.out.println(this.gameBoard.getPlayerVLocations());
            printBoard();
            moveScore = minimax(this.gameBoard, move, depth-1, false);

            System.out.println(move);
            System.out.println(moveScore);
            System.out.println("MODIFIED");
            System.out.print("H: ");
            System.out.println(this.gameBoard.getPlayerHLocations());
            System.out.print("V: ");
            System.out.println(this.gameBoard.getPlayerVLocations());
            printBoard();

            if (Math.abs(moveScore) > Math.abs(maxScore)) {
                maxScore = moveScore;   // update maxScore
                bestMove = move;        // update bestMove
            } else if (moveScore == maxScore) { // tie-break
                bestMove = move;
            }

            System.out.println("REVERSEDjhjhj");
            reverse(move);
            System.out.print("H: ");
            System.out.println(this.gameBoard.getPlayerHLocations());
            System.out.print("V: ");
            System.out.println(this.gameBoard.getPlayerVLocations());
            printBoard();
        }


//        minimax(this.gameBoard, possMoves.get(i), depth--, true);
//        (* Initial call for maximizing player *)
//        minimax(origin, depth, TRUE)

        // System.out.println(generateMoves(this.p));
        // System.out.println("Move " + move);
        // System.out.println("Number of possible moves " + numLegalMoves(this.p, this.gameBoard));

        System.out.println("About to end");

        System.out.println(bestMove.i + "," + bestMove.j);
        System.out.println(this.gameBoard.getBoard()[bestMove.i][bestMove.j].getPieceTypeChar());

        System.out.println("Move I'm going to make " + bestMove);



        System.out.println("possible moves " + possMoves);
        System.out.print("H: ");
        System.out.println(this.gameBoard.getPlayerHLocations());
        System.out.print("V: ");
        System.out.println(this.gameBoard.getPlayerVLocations());



        this.update(bestMove);
        return bestMove;
    }



    /**
     * Attempt at depth limited minimax
     * @return move
     */
    private int minimax(Board board, Move move, int depth, boolean maximizingPlayer) {
        int score = evaluateMove(move);

        // apply move to update the board
        this.update(move);

        // Base Case 1 - Reached Specified Depth of Search
        // Base Case 2 - Reached End of Game (No player V or H locations
        if (depth == 0 || board.getPlayerHLocations().size() == 0 || board.getPlayerVLocations().size() == 0) {
            System.out.println("HIT BOTTOM. Score: " + score);
            return score;
        }

        int bestScore = 0, moveScore;
        boolean first = true;

        if (maximizingPlayer) {
            // generates all possible moves for maximizing player
            this.possMoves = generateMoves(this.p);

            System.out.println("MAX: Going to depth" + (depth-1));

            // Return highest score of moves.
            for (Move v : possMoves) {
                if (first) {
                    bestScore = minimax(this.gameBoard, possMoves.get(0), depth-1, false);
                    first = false;
                } else {

                    System.out.println("MAX: Going to depth" + (depth - 1));

                    moveScore = minimax(board, v, depth - 1, false);

                    if (moveScore > bestScore) {
                        bestScore = moveScore;
                    }

                    reverse(v);
                }
            }
            return bestScore;
        } else {
            // generates all possible moves for minimizing player
            this.possMoves = generateMoves(this.o);

            System.out.println("MIN: Going to depth" + (depth-1));

            for (Move v : possMoves) {
                if (first) {
                    bestScore = minimax(this.gameBoard, possMoves.get(0), depth-1, true);
                    first = false;
                } else {

                    System.out.println("MIN: Going to depth" + (depth - 1));

                    moveScore = minimax(board, v, depth - 1, true);

                    if (moveScore < bestScore) {
                        bestScore = moveScore;
                    }

                    reverse(v);
                }
            }
            return bestScore;
        }
    }




//        01 function minimax(node, depth, maximizingPlayer)
//        02     if depth = 0 or node is a terminal node
//        03         return the heuristic value of node
//
//        04     if maximizingPlayer
//        05         bestValue := −∞
//        06         for each child of node
//        07             v := minimax(child, depth − 1, FALSE)
//        08             bestValue := max(bestValue, v)
//        09         return bestValue
//
//        10     else    (* minimizing player *)
//        11         bestValue := +∞
//        12         for each child of node
//        13             v := minimax(child, depth − 1, TRUE)
//        14             bestValue := min(bestValue, v)
//        15         return bestValue

    // 01 function alphabeta(node, depth, α, β, maximizingPlayer)
// 02      if depth = 0 or node is a terminal node
// 03          return the heuristic value of node
// 04      if maximizingPlayer
// 05          v := -∞
// 06          for each child of node
// 07              v := max(v, alphabeta(child, depth – 1, α, β, FALSE))
// 08              α := max(α, v)
// 09              if β ≤ α
// 10                  break (* β cut-off *)
// 11          return v
// 12      else
// 13          v := +∞
// 14          for each child of node
// 15              v := min(v, alphabeta(child, depth – 1, α, β, TRUE))
// 16              β := min(β, v)
// 17              if β ≤ α
// 18                  break (* α cut-off *)
// 19          return v
// (* Initial call *)
// alphabeta(origin, depth, -∞, +∞, TRUE)

//        int bestScore;
//        int currentScore;
//        Move bestMove = null;
//
//        if (depth == 0 || move == null) {
//            return move;
//        }
//
//        if (maximizingPlayer) {
//            Move bestMove = null;
//            // loop through possible moves from this state
//            currentMove = minimax(board, nextMove, depth - 1, false);
//            if (currentMove > bestMove) {
//                bestMove = currentMove;
//            }
//        }

    /**
     * Calculate the number of legal moves for player
     * THIS IS PROBABLY NOT NEEDED
     * @param player The player that is getting its number of moves calculated
     */
    private int numLegalMoves(char player, Board board) {
        int num_moves = 0;

        if (player == 'H') {
            for (int i = 0; i < board.getPlayerHLocations().size(); i++) {
                // retrieve piece location(s)
                double tmpX = board.getPlayerHLocations().get(i).getX();
                double tmpY = board.getPlayerHLocations().get(i).getY();

                Cell tmpCell = board.getBoard()[(int) tmpX][(int) tmpY];

                num_moves += tmpCell.getSurrounding(tmpCell.getPieceTypeChar(), board);
            }
        }

        if (player == 'V') {
            for (int i = 0; i < board.getPlayerVLocations().size(); i++) {
                // retrieve piece location(s)
                double tmpX = board.getPlayerVLocations().get(i).getX();
                double tmpY = board.getPlayerVLocations().get(i).getY();

                Cell tmpCell = board.getBoard()[(int) tmpX][(int) tmpY];

                num_moves += tmpCell.getSurrounding(tmpCell.getPieceTypeChar(), board);
            }
        }

        return num_moves;
    }

    /**
     * Get the actual surrounding possible moves
     * @param player The player that is getting possible next moves generated
     */
    private List<Move> generateMoves(char player) {
        List<Move> nextMoves = new ArrayList<Move>();   // allocate list

        if (player == 'H') {
            for (int i = 0; i < this.gameBoard.getPlayerHLocations().size(); i++) {
                // retrieve piece location(s)
                double tmpX = this.gameBoard.getPlayerHLocations().get(i).getX();
                double tmpY = this.gameBoard.getPlayerHLocations().get(i).getY();

                // checks above, avoids top most row
                if (tmpY != gameBoard.getBoardSize() - 1 && !gameBoard.getBoard()[(int) tmpX][(int) tmpY + 1].isBlocked()) {
//                    nextMoves.add(new Move((int)tmpX, (int)tmpY + 1, Move.Direction.UP));
                    nextMoves.add(new Move((int)tmpX, (int)tmpY, Move.Direction.UP));
                }

                // checks right and finish line
                if (tmpX == gameBoard.getBoardSize() - 1) {
//                    nextMoves.add(new Move((int)tmpX + 1, (int)tmpY, Move.Direction.RIGHT));
                    nextMoves.add(new Move((int)tmpX, (int)tmpY, Move.Direction.RIGHT));
                } else if (!gameBoard.getBoard()[(int) tmpX + 1][(int) tmpY].isBlocked()) {
//                    nextMoves.add(new Move((int)tmpX + 1, (int)tmpY, Move.Direction.RIGHT));
                    nextMoves.add(new Move((int)tmpX, (int)tmpY, Move.Direction.RIGHT));
                }

                // checks below, avoids bottom most row
                if (tmpY != 0 && !gameBoard.getBoard()[(int) tmpX][(int) tmpY - 1].isBlocked()) {
//                    nextMoves.add(new Move((int)tmpX, (int)tmpY - 1, Move.Direction.DOWN));
                    nextMoves.add(new Move((int)tmpX, (int)tmpY, Move.Direction.DOWN));
                }
            }
        }

        if (player == 'V') {
            for (int i = 0; i < this.gameBoard.getPlayerVLocations().size(); i++) {
                // retrieve piece location(s)
                double tmpX = this.gameBoard.getPlayerVLocations().get(i).getX();
                double tmpY = this.gameBoard.getPlayerVLocations().get(i).getY();

                // checks left, avoids far left column
                if (tmpX != 0 && !gameBoard.getBoard()[(int) tmpX - 1][(int) tmpY].isBlocked()) {
//                    nextMoves.add(new Move((int)tmpX - 1, (int)tmpY, Move.Direction.LEFT));
                    nextMoves.add(new Move((int)tmpX, (int)tmpY, Move.Direction.LEFT));
                }

                // checks above and finish line
                if (tmpY == gameBoard.getBoardSize() - 1) {
//                    nextMoves.add(new Move((int)tmpX, (int)tmpY + 1, Move.Direction.UP));
                    nextMoves.add(new Move((int)tmpX, (int)tmpY, Move.Direction.UP));
                } else if (!gameBoard.getBoard()[(int) tmpX][(int) tmpY + 1].isBlocked()) {
//                    nextMoves.add(new Move((int)tmpX, (int)tmpY + 1, Move.Direction.UP));
                    nextMoves.add(new Move((int)tmpX, (int)tmpY, Move.Direction.UP));
                }

                // checks right, avoids far right column
                if (tmpX != gameBoard.getBoardSize() - 1
                        && !gameBoard.getBoard()[(int) tmpX + 1][(int) tmpY].isBlocked()) {
//                    nextMoves.add(new Move((int)tmpX + 1, (int)tmpY, Move.Direction.RIGHT));
                    nextMoves.add(new Move((int)tmpX, (int)tmpY, Move.Direction.RIGHT));
                }
            }
        }

        return nextMoves;
    }

    /**
     * The heuristic evaluation function for the current board
     * @param move the move being evaluated
     * @return heuristic value
     */
    private int evaluateMove(Move move) {
        int score;
        int myMobility = 0;
        int oppMobility = 0;

        if (this.p == 'H') {
            myMobility = numLegalMoves('H', this.gameBoard);
            oppMobility = numLegalMoves('V', this.gameBoard);
        } else if (this.p == 'V') {
            myMobility = numLegalMoves('V', this.gameBoard);
            oppMobility = numLegalMoves('H', this.gameBoard);
        }

        // cell that is being moved away from
        // Cell cell = this.gameBoard.getBoard()[move.i][move.j];
        // char tmpCellChar = cell.getPieceTypeChar();

        // // cell that is being moved to
        // int to_i = move.i, to_j = move.j;
        // switch(move.d) {
        //     case UP:    to_j++; break;
        //     case DOWN:  to_j--; break;
        //     case RIGHT: to_i++; break;
        //     case LEFT:  to_i--; break;
        // }

        // // moving to a location which is off the board?
        // if (cell.getPieceTypeChar() == 'H' && to_i == this.d) {
        //     // score value of finishing off a piece?
        // }
        // if (cell.getPieceTypeChar() == 'V' && to_j == this.d) {
        //     // score value of finishing off a piece?
        // }
        score = myMobility-oppMobility;
        return score;
    }

    /**
     * reverse the original move made by reverting the board state back to what is was before
     * @move the move that was made that now has to be reversed
     */
    private void reverse(Move move) {
        // no move was actually made prior, so don't reverse anything
        if (move == null) {
            return;
        }

        // original location
        int original_i = move.i, original_j = move.j;

        // where the piece/cell moved to
        int to_i = move.i, to_j = move.j;
        switch(move.d) {
            case UP:    to_j++; break;
            case DOWN:  to_j--; break;
            case RIGHT: to_i++; break;
            case LEFT:  to_i--; break;
        }

        // moved to a location which is off the board?
        if (to_i == this.d) {   // horizontal moving off
            // reinstate it back in
            this.gameBoard.getBoard()[original_i][original_j].setPieceTypeChar('H');

            // add point back into ArrayList - used to keep track of pieces
            this.gameBoard.getPlayerHLocations().add(new Point(original_i, original_j));

            return;
        }
        if (to_j == this.d) {   // vertical moving off
            // reinstate it back in
            this.gameBoard.getBoard()[original_i][original_j].setPieceTypeChar('V');

            // add point back into ArrayList - used to keep track of pieces
            this.gameBoard.getPlayerVLocations().add(new Point(original_i, original_j));

            return;
        }

        // cell which needs to be reversed
        Cell cell = this.gameBoard.getBoard()[to_i][to_j];
        char tmpCellChar = cell.getPieceTypeChar();

        // if here, piece did not move off the board, so instead of reinstating it,
        // just apply reversal of move
        this.gameBoard.updateBoard(to_i, to_j, '+');
        this.gameBoard.updateBoard(original_i, original_j, tmpCellChar);

        // update the player location arrayList
        if (tmpCellChar == 'H') {
            for (Iterator<Point> it = this.gameBoard.getPlayerHLocations().iterator(); it.hasNext();) {
                Point point = it.next();
                // find the point which is the one that's being moved off board
                if (point.getX() == to_i && point.getY() == to_j) {
                    it.remove();
                }
            }
        }
        if (tmpCellChar == 'V') {
            for (Iterator<Point> it = this.gameBoard.getPlayerVLocations().iterator(); it.hasNext();) {
                Point point = it.next();
                // find the point which is the one that's being moved off board
                if (point.getX() == to_i && point.getY() == to_j) {
                    it.remove();
                }
            }
        }

        return;
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
            for (Iterator<Point> it = this.gameBoard.getPlayerHLocations().iterator(); it.hasNext();) {
                Point point = it.next();
                // find the point which is the one that's being moved off board
                if (point.getX() == move.i && point.getY() == move.j) {
                    it.remove();
                }
            }

            // this is used to update the board and make it free at this cell
            this.gameBoard.getBoard()[move.i][move.j].setBlock(false);

            return;
        }
        if (cell.getPieceTypeChar() == 'V' && to_j == this.d) {
            this.gameBoard.getBoard()[move.i][move.j].setPieceTypeChar('+');
            // removes point from ArrayList - used to keep track of pieces
            for (Iterator<Point> it = this.gameBoard.getPlayerVLocations().iterator(); it.hasNext();) {
                Point point = it.next();
                // find the point which is the one that's being moved off board
                if (point.getX() == move.i && point.getY() == move.j) {
                    it.remove();
                }
            }

            // this is used to update the board and make it free at this cell
            this.gameBoard.getBoard()[move.i][move.j].setBlock(false);

            return;
        }

        // make move (not off board) - update board
        this.gameBoard.updateBoard(move.i, move.j, '+');
        this.gameBoard.updateBoard(to_i, to_j, tmpCellChar);

        // this is actually needed!
        // used to update the player arrayList locations
        // different to above because this gets called all the time, even if nothing is moving off board
        if (tmpCellChar == 'H') {
            for (Iterator<Point> it = this.gameBoard.getPlayerHLocations().iterator(); it.hasNext();) {
                Point point = it.next();
                // find the point which is the one that's being moved off board
                if (point.getX() == move.i && point.getY() == move.j) {
                    it.remove();
                }
            }
        }
        if (tmpCellChar == 'V') {
            for (Iterator<Point> it = this.gameBoard.getPlayerVLocations().iterator(); it.hasNext();) {
                Point point = it.next();
                // find the point which is the one that's being moved off board
                if (point.getX() == move.i && point.getY() == move.j) {
                    it.remove();
                }
            }
        }


        //        Used for testing
//        System.out.println(this.p);
//        Cell[][] test = this.gameBoard.getBoard();
//        for (int j = this.d-1; j >= 0; j--) {
//            for (int i = 0; i < this.d; i++) {
//                System.out.print(test[i][j].getPieceTypeChar() + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.print("DONE\n");

        System.out.println("FUCK YOU");
        System.out.print("H: ");
        System.out.println(this.gameBoard.getPlayerHLocations());
        System.out.print("V: ");
        System.out.println(this.gameBoard.getPlayerVLocations());
        return;
    }

    private void printBoard() {
        Cell[][] test = this.gameBoard.getBoard();
        for (int j = this.d-1; j >= 0; j--) {
            for (int i = 0; i < this.d; i++) {
                System.out.print(test[i][j].getPieceTypeChar() + " ");
            }
            System.out.print("\n");
        }
    }

}
