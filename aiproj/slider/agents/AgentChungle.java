package aiproj.slider.agents;

import aiproj.slider.Move;
import aiproj.slider.agents.helper.MoveListComparator;
import aiproj.slider.agents.helper.MoveManager;
import aiproj.slider.board.Board;
import aiproj.slider.board.Cell;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by hao on 17/5/17.
 */
public class AgentChungle extends Agent {

    /* heuristic - minimax*/
    private double halfway = (dimension / 2.0);
    private double prevMaxDistH = Double.POSITIVE_INFINITY;
    private double prevMaxDistV = Double.POSITIVE_INFINITY;
    private double prevPiecesH = Double.POSITIVE_INFINITY;
    private double prevPiecesV = Double.POSITIVE_INFINITY;

    int depth; // for minimax search

    /* Used for monte carlo simulation */
    private Random rng;
    private Move move;

    private double x = 1000;
    private double wins = 0;

    private List<Move> monteMoves;
    private List<Move> hMoves;
    private List<Move> vMoves;

    private Board origBoard;
    private Board tempBoard;

    private Move bestMove;
    private double bestScore;

    /******/

    private enum GameStage {
        EARLYGAME, MIDGAME, ENDGAME
    }

    private GameStage stage = GameStage.EARLYGAME;

    @Override
    public Move move() {
        switch (this.stage) {
            default:
            case EARLYGAME:
                return minimaxStrat(4);

            case MIDGAME:
                return minimaxStrat(10);

            case ENDGAME:
                return endStrat();
        }
    }

    @Override
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

            /* check if game is to transition to next stage */
            checkStage();
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

            /* check if game is to transition to next stage */
            checkStage();
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

        /* check if game is to transition to next stage */
        checkStage();
    }

    public Move minimaxStrat(int d) {
        MoveManager tmpMove;
        MoveManager bestMove = null;
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;

        this.depth = d;

        /* generates the possible moves the player can make at it's current state */
        this.possMoves = generateMoves(this.player, this.gameBoard);
        if (this.player == 'H') {
            Collections.sort(possMoves, MoveListComparator.HComparator);
        } else {
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

        System.out.println(possMoves + " " + bestMove);
        this.update(bestMove.getMove());
        hm.put(gameBoard.toString(),1);

        return bestMove.getMove();
    }

    public Move endStrat() {

        Move tmpMonte = monteMove(this.player, this.gameBoard);

        this.update(tmpMonte);
        return tmpMonte;
    }

    private MoveManager minimax(Move m, int d, boolean mp, double alpha, double beta) {

        MoveManager tmpMove = null;
        MoveManager maxMove = null;
        MoveManager minMove = null;

        /* apply move to update player's internal representation of the board */
        update(m);

        if (hm.containsKey(gameBoard.toString())){
            MoveManager move = new MoveManager(m, -20000);
            return move;
        }

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
                //PenaltyPoints for being in the starting third of the board
                if ((int) p.getX() < ((dimension-1)/3.0)*dimension) {
                    penaltypoints += ((dimension-1)/3.0)*dimension-p.getX();
                }
            }
        }

        for (Point p : playerVLoc) {
            Vscore += p.getY();
            if (player == 'V') {
                //PenaltyPoints for being in the starting third of the board
                if ((int) p.getY() < ((dimension-1)/3.0)*dimension) {
                    penaltypoints += ((dimension-1)/3.0)*dimension-p.getY();
                }
            }
        }


        if (this.player == 'H') {
            if (Hscore+(dimension - playerHLoc.size())*dimension > dimension*(dimension-1)-(((dimension-1)/3.0)*dimension)) {
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
            if (Vscore+(dimension - playerVLoc.size())*dimension > dimension*(dimension-1)-(((dimension-1)/3.0)*dimension)){
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

    public Move monteMove(char player, Board b) {
        this.tempBoard = new Board(this.dimension, b.toString().split("\n"));
        bestMove = null;    // move being returned
        bestScore = 0;

        boolean hTurn;
        boolean simulate = true;
        boolean first = true;

        /* for simulation in monte carlo */
        long seed = System.nanoTime();
        rng = new Random(seed);

        /* identifies who's turn it is */
        if (player == 'H') {
            hTurn = true;
        } else {
            hTurn = false;
        }

        monteMoves = generateMoves(player, b);

        /* no possible moves */
        if (monteMoves.size() == 0) {
            return null;
        }

        /* look at all the moves possible at this state */
        for (Move m : generateMoves(player, b)) {

            /* simulate each x number of times */
            for (int i = 0; i < x; i++) {
                simulate = true;
                first = true;

                // while no one has won yet
                while (simulate) {
                    if (hTurn) {
                        if (first && player == 'H') {
                            tempBoard.update(tempBoard, m);
                            first = false;
                        } else {
                            // simulation
                            hMoves = generateMoves('H', tempBoard);

                            // check if no moves possible
                            if (hMoves.size() == 0) {
                                if (numLegalMoves('H', tempBoard) == 0) {
                                    hTurn = false;
                                    continue;   // pass
                                }
                            }

                            move = hMoves.get(rng.nextInt(hMoves.size()));

                            tempBoard.update(tempBoard, move);
                        }

                        if (tempBoard.getPlayerHLocations().size() == 0) { // no more remaining, game over
                            if (player == 'H') {
                                wins += 1;
                            }
                            simulate = false;
                        }

                        hTurn = false;
                    } else {
                        if (first && player == 'V') {
                            tempBoard.update(tempBoard, m);
                            first = false;
                        } else {
                            vMoves = generateMoves('V', tempBoard);

                            // check if no moves possible
                            if (vMoves.size() == 0) {
                                if (numLegalMoves('V', tempBoard) == 0) {
                                    hTurn = true;
                                    continue;   // pass
                                }
                            }

                            move = vMoves.get(rng.nextInt(vMoves.size()));

                            tempBoard.update(tempBoard, move);
                        }

                        if (tempBoard.getPlayerVLocations().size() == 0) { // no more, game over
                            if (player == 'V') {
                                wins += 1;
                            }
                            simulate = false;
                        }

                        hTurn = true;
                    }
                }

                this.tempBoard = new Board(this.dimension, b.toString().split("\n"));

                if (player == 'H') {
                    hTurn = true;
                } else {
                    hTurn = false;
                }
            }

            if (wins/x > bestScore) {
                bestScore = (wins/x);
                bestMove = m;

                if (bestScore == 100.0) {
                    return bestMove;
                }
            }

            wins = 0;

        }

        return bestMove;
    }

    private void checkStage() {
        if ((this.gameBoard.getPlayerHLocations().size() <= 3 && this.player == 'H') &&
                (this.gameBoard.getPlayerHLocations().size() + this.gameBoard.getPlayerVLocations().size() <= 5)) {
            /* monte carlo (this is used for the end game)
             * NOTE: Will have to fine tune this or add in a safety guard just to make sure we don't use up all our
             *       time evaluating monte carlo
             */
            this.stage = GameStage.ENDGAME;
        } else if ((this.gameBoard.getPlayerVLocations().size() <= 3 && this.player == 'V') &&
                (this.gameBoard.getPlayerHLocations().size() + this.gameBoard.getPlayerVLocations().size() <= 5)) {
            /* monte carlo (this is used for the end game)
             * NOTE: Will have to fine tune this or add in a safety guard just to make sure we don't use up all our
             *       time evaluating monte carlo
             */
            this.stage = GameStage.ENDGAME;
        } else if (this.gameBoard.getPlayerHLocations().size() <= (this.dimension/2) || this.gameBoard.getPlayerVLocations().size() <= (this.dimension/2)) {
            /* mid game */
            this.stage = GameStage.MIDGAME;
        } else {
            /* early game */
            this.stage = GameStage.EARLYGAME;
        }
    }

}
