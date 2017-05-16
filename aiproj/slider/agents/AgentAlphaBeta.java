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

import aiproj.slider.agents.helper.EvaluationFunctions;
import aiproj.slider.agents.helper.MoveListComparator;
import aiproj.slider.agents.helper.MoveManager;
import aiproj.slider.board.Board;
import aiproj.slider.board.Cell;
import aiproj.slider.Move;
import aiproj.slider.SliderPlayer;


public class AgentAlphaBeta extends Agent {
    double halfway = dimension/2.0;
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

        if (this.gameBoard.getPlayerHLocations().size() <= 2 && (this.gameBoard.getPlayerHLocations().size() + this.gameBoard.getPlayerVLocations().size() <= 5)) {
            depth = 10;
        } else if (this.gameBoard.getPlayerVLocations().size() <= 2 && (this.gameBoard.getPlayerHLocations().size() + this.gameBoard.getPlayerVLocations().size() <= 5)) {
//            if here but we have much less pieces than opponent, just use a smaller depth otherwise too risky
            depth = 10;
        } else if (this.gameBoard.getPlayerHLocations().size() <= (this.dimension/2) || this.gameBoard.getPlayerVLocations().size() <= (this.dimension/2)) {
            // mid game
            depth = 8;
        } else {
            // while still in early stages of game
            depth = 6;
        }

        /* generates the possible moves the player can make at it's current state */
        this.possMoves = generateMoves(this.player);
        if (this.player == 'H'){
            Collections.sort(possMoves, MoveListComparator.HComparator);
        }
        else{
            Collections.sort(possMoves, MoveListComparator.VComparator);
        }
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

            if (tmpMove.getScore() > bestMove.getScore()) {
                bestMove.setMove(v);
                bestMove.setScore(tmpMove.getScore());
                alpha = bestMove.getScore();
            }

            if (beta <= alpha){
                break;
            }
        }
        System.out.println(possMoves + " " + bestMove);
        this.update(bestMove.getMove());

        return bestMove.getMove();
    }

    /**
     * Minimax search algorithm
     * @return int, score based on heuristic for the move player is considering
     */
    private MoveManager minimax(Move m, int d, boolean mp, double alpha, double beta) {

//        System.out.println("****DEPTH " + d + "****");
        MoveManager tmpMove = null;
        MoveManager maxMove = null;
        MoveManager minMove = null;

        /* apply move to update player's internal representation of the board */
        this.update(m);

//        if (move.getScore() > 20) {

//            System.out.println(move.getScore());
//            return move;
//        }

        /* Base Case 1 - Reached Specified Depth of Search
         * Base Case 2 - Reached End of Game (No player V or H pieces left) */
        if (d == 0 || this.gameBoard.getPlayerHLocations().size() == 0
                || this.gameBoard.getPlayerVLocations().size() == 0) {
            MoveManager move = new MoveManager(m, evaluateMove());
            return move;
        }

        if (mp) {
            /* generates all possible moves for maximizing player */
            List<Move> possMoves = generateMoves(this.player);
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
            List<Move> oppMoves = generateMoves(this.opponent);
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
                tmpMove.setScore(tmpMove.getScore());

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
        boolean upperhalf = true;
        int numpiecesinend = 0;

        EvaluationFunctions ef = new EvaluationFunctions();
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
            if (upperhalf && this.player == 'H' && p.getX() < halfway) {
                upperhalf = false;
            }
            if (player == 'H') {
                //PenaltyPoints for being on last column
                if ((int) p.getX() == 0) {
                    penaltypoints += 1;
                }
                //If enemy piece is on the end column
                if ((int) p.getX() == dimension - 1) {
                    for (Point p2 : playerVLoc) {
                        if ((int) p2.getX() == dimension - 1) {
                            numpiecesinend++;
                        }
                    }
                }
            }
        }

        for (Point p : playerVLoc) {
            Vscore += p.getY();
            if (upperhalf && this.player == 'V' && p.getY() < halfway) {
                upperhalf = false;
            }
            if (player == 'V') {
                if ((int) p.getY() == 0) {
                    penaltypoints += 1;
                }
                if ((int) p.getY() == dimension - 1) {
                    for (Point p2 : playerHLoc) {
                        if ((int) p2.getY() == dimension - 1) {
                            numpiecesinend++;
                        }
                    }
                }
            }
        }

        if (this.player == 'H') {
            if (score_winloss == 0) {
                //Don't forget about the H pieces which have finished
                if (upperhalf) {
                    if (playerHLoc.size() < dimension / 2) {
                        Hscore += (dimension - playerHLoc.size()) * dimension + 1;
                    } else {
                        Hscore += (dimension - playerHLoc.size()) * (dimension);
                    }

                } else {
                    Hscore += (dimension - playerHLoc.size()) * (dimension - 2);
                }
                Vscore += (dimension - playerVLoc.size()) * (dimension + 2);
            }
            else{
                Vscore += (dimension - playerVLoc.size()) * (dimension);
            }
            //Prioritise blocking their pieces over finishing
            return score_winloss + Hscore - Vscore - penaltypoints;
        } else {
            if (score_winloss == 0){
                if (upperhalf) {
                    if (playerVLoc.size() < dimension / 2) {
                        Vscore += (dimension - playerVLoc.size()) * dimension + 1;
                    } else {
                        Vscore += (dimension - playerVLoc.size()) * (dimension);
                    }

                } else {
                    Vscore += (dimension - playerVLoc.size()) * (dimension - 2);
                }
                Hscore += (dimension - playerHLoc.size()) * (dimension + 2);
            }
            else{
                Hscore += (dimension - playerHLoc.size()) * (dimension);
            }
            //Don't forget about the H pieces which have finished
            return score_winloss + Vscore - Hscore - penaltypoints;
        }
    }


        /* want majority of our pieces in front of the opponent to mark our territory */
//        if (this.player == 'H') {
//            for (int i = 0; i < playerHLoc.size(); i++) {
//                for (int j = 0; j < playerVLoc.size(); j++) {
//                    /* higher importance on moving right */
//                    // check if behind V's
//                    if (playerHLoc.get(i).getX() < playerVLoc.get(j).getX()) {
//                        score_pos -= 2;
//                    } else { // ahead of V's
//                        score_pos += 4;
//                    }
//
//                    if (playerHLoc.get(i).getY() < playerVLoc.get(j).getY()) {
//                        score_pos -= 1;
//                    } else {
//                        score_pos += 2;
//                    }
//                }
//            }
//        } else {
//            for (int i = 0; i < playerVLoc.size(); i++) {
//                for (int j = 0; j < playerHLoc.size(); j++) {
//                    /* higher importance on moving up */
//                    // check if behind H's
//                    if (playerVLoc.get(i).getY() < playerHLoc.get(j).getY()) {
//                        score_pos -= 2;
//                    } else { // ahead of H's
//                        score_pos += 4;
//                    }
//
//                    if (playerVLoc.get(i).getY() < playerHLoc.get(j).getY()) {
//                        score_pos -= 1;
//                    } else {
//                        score_pos += 2;
//                    }
//                }
//            }
//        }

        //Calculates mobility of players' pieces
//        myMobility = numBlocks(this.player, this.gameBoard);
//        oppMobility = numBlocks(this.opponent, this.gameBoard);
//        score_mob = myMobility - oppMobility;

//        if (this.player == 'H'){
//            score_pos = ef.evaluatePiecePos(this.player, playerHLoc, dimension) -
//                    ef.evaluatePiecePos(this.opponent, playerVLoc, dimension);
//        } else {
//            score_pos = ef.evaluatePiecePos(this.player, playerVLoc, dimension) -
//                    ef.evaluatePiecePos(this.opponent, playerVLoc, dimension);
//        }

//        return score_mob+score_pos+score_pieces;

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
