/* Hao Le - leh2
 * Sam Chung - chungs1
 *
 * Player Class - Not used currently for project A
 *                will probably be needed for project B
 * Last Modified: 07/04/17
 *
 */

package aiproj.slider.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.awt.Point;

import aiproj.slider.agents.helper.MoveListComparator;
import aiproj.slider.agents.helper.MoveManager;
import aiproj.slider.board.Cell;
import aiproj.slider.Move;

public class AgentAlphaBeta extends Agent {

    //private double halfway = (dimension / 2.0);
    private double prevMaxDistH = Double.POSITIVE_INFINITY;
    private double prevMaxDistV = Double.POSITIVE_INFINITY;
    private double prevPiecesH = Double.POSITIVE_INFINITY;
    private double prevPiecesV = Double.POSITIVE_INFINITY;

    AgentMonteCarlo monte = new AgentMonteCarlo();

    /**
     * Dictates the move for the player, this agent makes use of the
     * minimax (with a-b pruning) algorithm
     * @return move
     */
    @Override
    public Move move() {

        MoveManager tmpMove;
        MoveManager bestMove = null;
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;

        int depth; // for minimax search

        if (this.gameBoard.getPlayerHLocations().size() <= 3 && this.player == 'H') {
//            depth = 10;
            System.out.println("OVER HERE");
            monte.init(this.dimension, this.gameBoard, this.player);
            Move tmpMonte = monte.getMove(this.player, this.gameBoard);
            this.update(tmpMonte);
            return tmpMonte;
        } else if (this.gameBoard.getPlayerVLocations().size() <= 2 && (this.gameBoard.getPlayerHLocations().size() + this.gameBoard.getPlayerVLocations().size() <= 5)) {
//            if here but we have much less pieces than opponent, just use a smaller depth otherwise too risky
            depth = 10;
        } else if (this.gameBoard.getPlayerHLocations().size() <= (this.dimension/2) || this.gameBoard.getPlayerVLocations().size() <= (this.dimension/2)) {
            // mid game
            depth = 5;
        } else {
            // while still in early stages of game
            depth = 3;
        }

        /* generates the possible moves the player can make at it's current state */
        this.possMoves = generateMoves(this.player, this.gameBoard);
        if (this.player == 'H'){
            Collections.sort(possMoves, MoveListComparator.HComparator);
        }
        else{
            Collections.sort(possMoves, MoveListComparator.VComparator);
        }

        /* no moves are possible, pass turn */
        if (this.possMoves.size() == 0) {
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

            if (tmpMove.getScore() > bestMove.getScore()) {
                bestMove.setMove(v);
                bestMove.setScore(tmpMove.getScore());
                alpha = bestMove.getScore();
            }

            if (beta <= alpha){
                break;
            }
        }

        this.update(bestMove.getMove());
        hm.put(gameBoard.toString(),1);
      
        return bestMove.getMove();
    }

    /**
     * Minimax search algorithm
     * @return int, score based on heuristic for the move player is considering
     */
    private MoveManager minimax(Move m, int d, boolean mp, double alpha, double beta) {

        MoveManager tmpMove = null;
        MoveManager maxMove = null;
        MoveManager minMove = null;

        /* apply move to update player's internal representation of the board */
        this.update(m);

        if (hm.containsKey(gameBoard.toString())){
            MoveManager move = new MoveManager(m, -20000);
            return move;
        };

        /* Base Case 1 - Reached Specified Depth of Search
         * Base Case 2 - Reached End of Game (No player V or H pieces left) */
        if (d == 0 || this.gameBoard.getPlayerHLocations().size() == 0
                || this.gameBoard.getPlayerVLocations().size() == 0) {
            MoveManager move = new MoveManager(m, evaluateMove());
            return move;
        }

        if (mp) {
            /* generates all possible moves for maximizing player */
            List<Move> possMoves = generateMoves(this.player, this.gameBoard);
            if (this.player == 'H'){
                Collections.sort(possMoves, MoveListComparator.HComparator);
            }
            else{
                Collections.sort(possMoves, MoveListComparator.VComparator);
            }

            /* no moves possible */
            if (possMoves.size() == 0) {
                tmpMove = minimax(null, d - 1, false, alpha, beta);
                return tmpMove;
            }

            /* return highest score of moves.*/
            for (Move v : possMoves) {
                /* no bestMove is found yet, assign first to it */
                tmpMove = minimax(v, d - 1, false, alpha, beta);
                reverse(v);

                if (maxMove == null) {
                    maxMove = new MoveManager(v, tmpMove.getScore());
                    alpha = maxMove.getScore();
                }

                if (tmpMove.getScore() > maxMove.getScore()) {
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
            List<Move> oppMoves = generateMoves(this.opponent, this.gameBoard);
            if (this.player == 'H'){
                Collections.sort(oppMoves, MoveListComparator.HComparator);
            }
            else{
                Collections.sort(oppMoves, MoveListComparator.VComparator);
            }

            /* no moves possible */
            if (oppMoves.size() == 0) {
                tmpMove = minimax(null, d - 1, true, alpha, beta);
                return tmpMove;
            }

            /* return lowest score of moves.*/
            for (Move v : oppMoves) {
                /* no bestMove is found yet, assign first to it */
                tmpMove = minimax(v, d - 1, true, alpha, beta);
                reverse(v);

                if (minMove == null) {
                    minMove = new MoveManager(v, tmpMove.getScore());
                    beta = minMove.getScore();
                }

                if (tmpMove.getScore() < minMove.getScore()) {
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

    /**
     * Manhattan distance - used for heuristic evaluation
     * @param x1 end point x
     * @param x0 start point x
     * @param y1 end point y
     * @param y0 start point y
     * @return value of distance from (x0, y0) to (x1, y1)
     */
    private int manhattanDistance(int x1, int x0, int y1, int y0) {
        return Math.abs(x1 - x0) + Math.abs(y1 - y0);
    }

    /**
     * The heuristic evaluation function for the current board
     * @return heuristic value
     */
    private double evaluateMove() {
        double Hscore = 0;
        double Vscore = 0;
        double score_winloss = 0;
        double penaltypoints = 0;

        ArrayList<Point> playerHLoc = gameBoard.getPlayerHLocations();
        ArrayList<Point> playerVLoc = gameBoard.getPlayerVLocations();

        if (this.player == 'H') {
            //Checks the board state is a win or loss
            if (playerHLoc.size() == 0) {
                score_winloss += 10000;
            }
            //Encourages the lose with dignity
            if (playerVLoc.size() == 0) {
                score_winloss += -10000;
            }

        }
        if (this.player == 'V') {

            if (playerVLoc.size() == 0) {
                score_winloss += 10000;
            }

            if (playerHLoc.size() == 0) {
                //Lose with dignity
                score_winloss += -10000;
            }
        }

        for (Point p : playerHLoc) {
            Hscore += p.getX();
            if (player == 'H') {
                //PenaltyPoints for being on the starting column
                if ((int) p.getX() == 0) {
                    penaltypoints += 1;
                }
            }
        }

        for (Point p : playerVLoc) {
            Vscore += p.getY();
            if (player == 'V') {
                //PenaltyPoints for being on the starting row
                if ((int) p.getY() == 0) {
                    penaltypoints += 1;
                }
            }
        }


        if (this.player == 'H') {
            if (Hscore+(dimension - playerHLoc.size())*dimension > (dimension-((dimension-1)/4.0))*dimension) {
                //If pieces are more than 3/4 of the way across the board
                Hscore += (dimension - playerHLoc.size()) * (dimension+1);
                //Priority of blocking.
                Vscore += (dimension - playerVLoc.size()) * (dimension);
            }

            else if (Hscore+(dimension - playerHLoc.size())*dimension > ((dimension-1)/2.0)*dimension){
                //If pieces are more than half way across the board.
                Hscore += (dimension - playerHLoc.size()) * (dimension-1);
                Vscore += (dimension - playerVLoc.size()) * (dimension+1);
            }
            else {
                //Starting Play
                Hscore += (dimension - playerHLoc.size()) * (dimension - 1);
                Vscore += (dimension - playerVLoc.size()) * (dimension + 2);
            }
            return score_winloss + Hscore - Vscore - penaltypoints;

        } else {
            if (Vscore+(dimension - playerVLoc.size())*dimension > (dimension-((dimension-1)/4.0))*dimension){
                //If pieces are more than 3/4 of the way across the board
                Vscore += (dimension - playerVLoc.size()) * (dimension+1);
                Hscore += (dimension - playerHLoc.size()) * (dimension);
            }
            if (Vscore+(dimension - playerVLoc.size())*dimension > ((dimension-1)/2.0)*dimension) {
                //If pieces are more than half way across the board.
                Vscore += (dimension - playerVLoc.size()) * (dimension-1);
                Hscore += (dimension - playerHLoc.size()) * (dimension+1);
            } else {
                Vscore += (dimension - playerVLoc.size()) * (dimension - 1);
                Hscore += (dimension - playerHLoc.size()) * (dimension + 2);
            }
            return score_winloss + Vscore - Hscore - penaltypoints;
        }

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
}
