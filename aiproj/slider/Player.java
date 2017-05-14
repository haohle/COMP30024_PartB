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
import java.util.Collections;

public class Player implements SliderPlayer {

    /* Player */
    private char player;
    /* Opponent */
    private char opponent;

    /* Player's internal gameboard */
    private int dimension;
    private String rawBoard[];
    private Board gameBoard;

    /* Used for random move agent */
    private Random rng;

    /* List of all possible makes the player can make in current state */
    private List<Move> possMoves;
    private List<Move> oppMoves;

    /**
     * Initialises the player
     */
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

        /* for random move agent */
        long seed = System.nanoTime();
        rng = new Random(seed);
    }

    /**
     * Dictates the move for the player, this agent makes use of the
     * minimax (with a-b pruning) algorithm
     * @return move
     */
    public Move move() {

        MoveManager tmpMove;
        MoveManager bestMove = null;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;


        int depth; // for minimax search

        if (this.gameBoard.getPlayerHLocations().size() <= 2 || this.gameBoard.getPlayerVLocations().size() <= 2) {
            depth = 9;
        } else if (this.gameBoard.getPlayerHLocations().size() <= (this.dimension/2) || this.gameBoard.getPlayerVLocations().size() <= (this.dimension/2)) {
            // mid game
            depth = 6;
        } else {
            // while still in early stages of game
            depth = 3;
        }

        /* generates the possible moves the player can make at it's current state */
        this.possMoves = generateMoves(this.player);
        Collections.sort(possMoves,new MoveListComparator());
//        System.out.println(this.possMoves);

        /* no moves are possible, pass turn */
        if (this.possMoves.size() == 0) {
            //System.out.println("*** NO MOVES POSSIBLE, PASSING ***");
            return null;
        }

        /* there is more than one possible move to make,
         * must evaluate which is the optimal move */
        for (Move v : this.possMoves) {
            tmpMove = minimax(v, depth - 1, false, alpha, beta);
            reverse(v);

            if (bestMove == null) {
                bestMove = new MoveManager(v, tmpMove.getScore());
                alpha = bestMove.getScore();
            }

            if (tmpMove.getScore() >= bestMove.getScore()) {
                bestMove.setMove(v);
                bestMove.setScore(tmpMove.getScore());
                alpha = bestMove.getScore();
            }

            if (beta <= alpha){
                System.out.println("BREAK EARLY" + beta + " " + alpha);
                break;
            }
        }

        /* updates the player's internal representation of the board */
        //printBoard();

        //System.out.println("ABOUT TO PRINT");
        //System.out.println(this.player);
//        System.out.println(bestMove);


        this.update(bestMove.getMove());

        return bestMove.getMove();

    }

    /**
     * Minimax search algorithm
     * @return int, score based on heuristic for the move player is considering
     */
    private MoveManager minimax(Move m, int d, boolean mp, int alpha, int beta) {


//        System.out.println("****DEPTH " + d + "****");

        MoveManager move = new MoveManager(m, evaluateMove(m));
        MoveManager tmpMove;
        MoveManager maxMove = null;
        MoveManager minMove = null;

        /* apply move to update player's internal representation of the board */
        this.update(move.getMove());

        if (move.getScore() > 20) {

//            System.out.println(move.getScore());
            return move;
        }

        /* Base Case 1 - Reached Specified Depth of Search
         * Base Case 2 - Reached End of Game (No player V or H pieces left) */
        if (d == 0 || this.gameBoard.getPlayerHLocations().size() == 0
                || this.gameBoard.getPlayerVLocations().size() == 0) {
            return move;
        }

        if (mp) {
            /* generates all possible moves for maximizing player */
            this.possMoves = generateMoves(this.player);
            Collections.sort(possMoves,new MoveListComparator());

//            System.out.println(this.possMoves);

            /* no moves possible */
            if (this.possMoves.size() == 0) {
                return move;
            }

            /* return highest score of moves.*/
            for (Move v : this.possMoves) {
                /* no bestMove is found yet, assign first to it */
                tmpMove = minimax(v, d - 1, false, alpha, beta);
                reverse(v);

                if (maxMove == null) {
                    maxMove = new MoveManager(v, tmpMove.getScore());
                    alpha = maxMove.getScore();
                }

                if (tmpMove.getScore() >= maxMove.getScore()) {
                    maxMove.setMove(v);
                    maxMove.setScore(tmpMove.getScore());
                    alpha = maxMove.getScore();
                }

                if (beta <= alpha){
                    break;
                }
            }
            return maxMove;
        } else {
            /* generates all possible moves for minimizing player */
            this.oppMoves = generateMoves(this.opponent);

            Collections.sort(oppMoves,new MoveListComparator());
//            System.out.println(this.oppMoves);

            /* no moves possible */
            if (this.oppMoves.size() == 0) {
                return move;
            }

            /* return lowest score of moves.*/
            for (Move v : this.oppMoves) {
                /* no bestMove is found yet, assign first to it */
                tmpMove = minimax(v, d - 1, true, alpha, beta);
                reverse(v);

                if (minMove == null) {
                    minMove = new MoveManager(v, tmpMove.getScore());
                    beta = minMove.getScore();
                }

                if (tmpMove.getScore() <= minMove.getScore()) {
                    minMove.setMove(v);
                    minMove.setScore(tmpMove.getScore());
                    beta = minMove.getScore();
                }

                if (beta <= alpha){
                    break;
                }
            }
            return minMove;
        }
    }
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

    /**
     * Calculate the number of legal moves for player
     * @param player The player that is getting its number of moves calculated
     */
    private int numLegalMoves(char player, Board board) {
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


    /**
     * Get the actual surrounding possible moves
     * @param player The player that is getting possible next moves generated
     */
    private List<Move> generateMoves(char player) {
        List<Move> nextMoves = new ArrayList<Move>();

        if (player == 'H') {
            for (int i = 0; i < this.gameBoard.getPlayerHLocations().size(); i++) {
                /* retrieve piece location(s) */
                double tmpX = this.gameBoard.getPlayerHLocations().get(i).getX();
                double tmpY = this.gameBoard.getPlayerHLocations().get(i).getY();

                /* checks above, avoids top most row */
                if (tmpY != this.gameBoard.getBoardSize() - 1
                        && !this.gameBoard.getBoard()[(int) tmpX][(int) tmpY + 1].isBlocked()) {
                    nextMoves.add(new Move((int) tmpX, (int) tmpY, Move.Direction.UP));
                }

                /* checks right and finish line */
                if (tmpX == this.gameBoard.getBoardSize() - 1) {
                    nextMoves.add(new Move((int) tmpX, (int) tmpY, Move.Direction.RIGHT));
                } else if (!this.gameBoard.getBoard()[(int) tmpX + 1][(int) tmpY].isBlocked()) {
                    nextMoves.add(new Move((int) tmpX, (int) tmpY, Move.Direction.RIGHT));
                }

                /* checks below, avoids bottom most row */
                if (tmpY != 0 && !this.gameBoard.getBoard()[(int) tmpX][(int) tmpY - 1].isBlocked()) {
                    nextMoves.add(new Move((int) tmpX, (int) tmpY, Move.Direction.DOWN));
                }
            }
        }

        if (player == 'V') {
            for (int i = 0; i < this.gameBoard.getPlayerVLocations().size(); i++) {
                /* retrieve piece location(s) */
                double tmpX = this.gameBoard.getPlayerVLocations().get(i).getX();
                double tmpY = this.gameBoard.getPlayerVLocations().get(i).getY();

                /* checks left, avoids far left column */
                if (tmpX != 0 && !gameBoard.getBoard()[(int) tmpX - 1][(int) tmpY].isBlocked()) {
                    nextMoves.add(new Move((int) tmpX, (int) tmpY, Move.Direction.LEFT));
                }

                /* checks above and finish line */
                if (tmpY == gameBoard.getBoardSize() - 1) {
                    nextMoves.add(new Move((int) tmpX, (int) tmpY, Move.Direction.UP));
                } else if (!gameBoard.getBoard()[(int) tmpX][(int) tmpY + 1].isBlocked()) {
                    nextMoves.add(new Move((int) tmpX, (int) tmpY, Move.Direction.UP));
                }

                /* checks right, avoids far right column */
                if (tmpX != gameBoard.getBoardSize() - 1
                        && !gameBoard.getBoard()[(int) tmpX + 1][(int) tmpY].isBlocked()) {
                    nextMoves.add(new Move((int) tmpX, (int) tmpY, Move.Direction.RIGHT));
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

        if (this.player == 'H') {
            myMobility = numLegalMoves('H', this.gameBoard);
            oppMobility = numLegalMoves('V', this.gameBoard);
        } else if (this.player == 'V') {
            myMobility = numLegalMoves('V', this.gameBoard);
            oppMobility = numLegalMoves('H', this.gameBoard);
        }

        score = myMobility - oppMobility;

        int to_i = move.i, to_j = move.j;
        switch (move.d) {
            case UP:
                to_j++;
                if (to_j == this.dimension) {
                    score += 100;
                    return score;
                }
                break;
            case DOWN:
                to_j--;
                break;
            case RIGHT:
                to_i++;
                if (to_i == this.dimension) {
                    score += 100;
                    return score;
                }
                break;
            case LEFT:
                to_i--;
                break;
        }

        if (this.player == 'H') {
            if (to_i > move.i) {
                score += 20;
            }
            if (to_j > move.j) {
                score += 10;
            }
//            if (to_j != 0) {
//                if (this.gameBoard.getBoard()[to_i][to_j-1].isBlocked()){
//                    // getting in the way of V
//                    score += 20;
//                }
//            }
        }
        if (this.player == 'V') {
            if (to_i > move.i) {
                score += 10;
            }
            if (to_j > move.j) {
                score += 20;
            }
//            if (to_i != 0) {
//                if (this.gameBoard.getBoard()[to_i-1][to_j].isBlocked()){
//                    // getting in the way of V
//                    score += 20;
//                }
//            }
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

        return score;
    }

    /**
     * Reverse the original move made by reverting the board state back to what is was before
     * @param move that was made that now has to be reversed
     */
    private void reverse(Move move) {
        /* no move was actually made prior, so don't reverse anything */
        if (move == null) {
            return;
        }

        /* original location */
        int original_i = move.i, original_j = move.j;

        /* where the piece/cell moved to */
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

        /* moved to a location which is off the board? */
        if (to_i == this.dimension) { // horizontal moving off
            /* reinstate it back in */
            this.gameBoard.getBoard()[original_i][original_j].setPieceTypeChar('H');
            /* add point back into ArrayList - used to keep track of pieces */
            this.gameBoard.getPlayerHLocations().add(new Point(original_i, original_j));

            this.gameBoard.getBoard()[original_i][original_j].setBlock(true);

            return;
        }

        if (to_j == this.dimension) { // vertical moving off
            /* reinstate it back in */
            this.gameBoard.getBoard()[original_i][original_j].setPieceTypeChar('V');
            /* add point back into ArrayList - used to keep track of pieces */
            this.gameBoard.getPlayerVLocations().add(new Point(original_i, original_j));

            this.gameBoard.getBoard()[original_i][original_j].setBlock(true);

            return;
        }

        /* cell/piece which needs to be reversed */
        Cell cell = this.gameBoard.getBoard()[to_i][to_j];
        char tmpCellChar = cell.getPieceTypeChar();

        /* if here, piece did not move off the board, so instead of reinstating it,
         * just apply reversal of move */
        this.gameBoard.updateBoard(to_i, to_j, '+');
        this.gameBoard.updateBoard(original_i, original_j, tmpCellChar);

        /* update the player location arrayList */
        if (tmpCellChar == 'H') {
            for (Iterator<Point> it = this.gameBoard.getPlayerHLocations().iterator(); it.hasNext(); ) {
                Point point = it.next();
                /* find the point which is the one that's being moved off board */
                if (point.getX() == to_i && point.getY() == to_j) {
                    it.remove();
                }
            }
        }

        if (tmpCellChar == 'V') {
            for (Iterator<Point> it = this.gameBoard.getPlayerVLocations().iterator(); it.hasNext(); ) {
                Point point = it.next();
                /* find the point which is the one that's being moved off board */
                if (point.getX() == to_i && point.getY() == to_j) {
                    it.remove();
                }
            }
        }
    }

    /**
     * Update the player's internal board
     * @param move that has been made
     */
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

    private void printBoard() {
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

}
