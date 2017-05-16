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

    private double halfway = (dimension / 2.0);
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
        double score_mob = 0;
        double score_pos = 0;
        double score_pieces = 0;
        double score_winloss = 0;

        int myMobility;
        int oppMobility;

        EvaluationFunctions ef = new EvaluationFunctions();
        ArrayList<Point> playerHLoc = gameBoard.getPlayerHLocations();
        ArrayList<Point> playerVLoc = gameBoard.getPlayerVLocations();

        double totalDist = 0;
        double manDist = 0;
        ArrayList<Double> distancesH = new ArrayList<>();

        if (this.player == 'H'){
            /* checks the board state for a won or loss */
            if (playerHLoc.size() == 0){
                score_winloss += 10000 - playerVLoc.size();
            }
            if (playerVLoc.size() == 0){
                score_winloss += -10000 + playerHLoc.size();
            }

//            /* checks for if there's less pieces on the board */
//            if (playerHLoc.size() < playerVLoc.size()) {
//                score_pieces += 100;
//            } else if (playerHLoc.size() > playerVLoc.size()) {
//                score_pieces -= 100;
//            } else {
//                // calc numLegalMoves here?
//            }
//
//            score_pieces += (dimension - playerHLoc.size()) * 20;
//
            // check photo gallery for picture of scenario need to handle
            // mobility difference should be able to handle it

            /* checks average distance of all pieces away from the end point */
            for (Point p : playerHLoc) {
                // find shortest possible path to end
                for (int i = 0; i < dimension; i++) {
                    manDist = manhattanDistance(dimension, (int) p.getX(), i, (int) p.getY());

                    // acount for obstacles in the way
                    // include B pieces too
                    // not accurate
                    for (Point o : playerVLoc) {
                        // something on the same row that will block this piece
                        if (i == o.getY()) {
                            if (p.getX() < o.getX()) {
                                manDist += 1;
//                                break; // only need to worry about the first thing blocking?
                            }
                        } else {
                            // clear line of sight
                            break;
                        }
                    }

                    distancesH.add(manDist);
                }

                Collections.sort(distancesH);   // sort in ascending order (smallest to largest)
                totalDist += distancesH.get(0); // shortest distance to end
            }
//            totalDist /= playerHLoc.size();
            if (totalDist < this.prevMaxDistH) {
                this.prevMaxDistH = totalDist;
                score_pos += 5;
            } else {
                score_pos -= 5;
            }

//            System.out.println(totalDist);
//
            /* want majority of our pieces in front of the opponent to mark our territory
             * might be less resource intensive to just average all piece Y locations?
             */
            for (Point p : playerHLoc) {
                for (Point o : playerVLoc) {
                    /* higher importance on moving up (y position), reward if above opponent */
                    if (p.getX() < o.getX()) {
                        score_pos -= 2;
                    } else {
                        score_pos += 3;
                    }

                    if (p.getY() < o.getY()) {
                        score_pos -= 1;
                    } else {
                        score_pos += 2;
                    }
                }
            }

            /* meaningful blocks in the last row or column before finishing piece */
            for (Point p1 : playerHLoc) {
                double tmpX1 = p1.getX();
                double tmpY1 = p1.getY();

                // check if in last column
                if (tmpX1 == dimension - 1) {
                    // check if any opponents under
                    for (Point p2 : playerVLoc) {
                        double tmpX2 = p2.getX();
                        double tmpY2 = p2.getY();

                        // strategically not finish move
                        if (tmpX1 == tmpX2 && tmpY1 > tmpY2) {
                            score_pos += 2;
                        }
                    }
                }
            }
        }

        if (this.player == 'V'){
            /* checks the board state for a won or loss */
            if (playerHLoc.size() == 0){
                score_winloss += -10000 + playerVLoc.size();
            }
            if (playerVLoc.size() == 0){
                score_winloss += 10000 - playerHLoc.size();
            }

//            /* checks for if there's less pieces on the board */
//            if (playerVLoc.size() < playerHLoc.size()) {
//                score_pieces += 100;
//            } else if (playerVLoc.size() > playerHLoc.size()) {
//                score_pieces -= 100;
//            } else {
//                // calc numLegalMoves here?
//            }
//
//            score_pieces += (dimension - playerHLoc.size()) * 20;
//
            /* checks average distance of all pieces away from the end point */
            for (Point p : playerVLoc) {
                totalDist += (dimension - p.getY());
            }
//            totalDist /= playerHLoc.size();
            if (totalDist < this.prevMaxDistV) {
                this.prevMaxDistV = totalDist;
                score_pos += 5;
            } else {
                score_pos -= 5;
            }
//
            /* want majority of our pieces in front of the opponent to mark our territory
             * might be less resource intensive to just average all piece Y locations?
             */
            for (Point p : playerVLoc) {
                for (Point o : playerHLoc) {
                    /* higher importance on moving up (y position), reward if above opponent */
                    if (p.getY() < o.getY()) {
                        score_pos -= 2;
                    } else {
                        score_pos += 3;
                    }

                    if (p.getX() < o.getX()) {
                        score_pos -= 1;
                    } else {
                        score_pos += 2;
                    }
                }
            }

            /* meaningful blocks in the last row or column before finishing piece */
            for (Point p1 : playerVLoc) {
                double tmpX1 = p1.getX();
                double tmpY1 = p1.getY();

                // check if in last row
                if (tmpY1 == dimension - 1) {
                    // check if any opponents under
                    for (Point p2 : playerHLoc) {
                        double tmpX2 = p2.getX();
                        double tmpY2 = p2.getY();

                        // strategically not finish move
                        if (tmpY1 == tmpY2 && tmpX1 > tmpX2) {
                            score_pos += 2;
                        } else {
                            score_pos -= 1;
                        }
                    }
                }
            }
        }

        double Hscore = 0;
        double Vscore = 0;
        double penaltypoints = 0;
        boolean upperhalf = true;

        for (Point p : playerHLoc) {
            Hscore += p.getX();
            if (upperhalf && this.player == 'H' && p.getX() < halfway) {
                upperhalf = false;
            }
        }

        for (Point p : playerVLoc) {
            Vscore += p.getY();
            if (upperhalf && this.player == 'V' && p.getY() < halfway) {
                upperhalf = false;
            }
        }

        if (this.player == 'H') {
            //Don't forget about the H pieces which have finished
            if (upperhalf) {
                Hscore += (dimension - playerHLoc.size()) * (dimension);
            } else {
                Hscore += (dimension - playerHLoc.size()) * (dimension - 2);
            }

            //Prioritise blocking their pieces over finishing
            Vscore += (dimension - playerVLoc.size()) * (dimension);
            return score_mob + score_pos + score_pieces + score_winloss + Hscore - Vscore - penaltypoints;
        } else {
            if (upperhalf) {
                Vscore += (dimension - playerVLoc.size()) * (dimension);
            } else {
                Vscore += (dimension - playerVLoc.size()) * (dimension - 2);
            }
            //Don't forget about the H pieces which have finished
            Hscore += (dimension - playerHLoc.size()) * (dimension);
            return score_mob + score_pos + score_pieces + score_winloss + Hscore - Vscore - penaltypoints;
        }

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
