package aiproj.slider.agents;

import aiproj.slider.Move;
import aiproj.slider.agents.helper.MoveManager;
import aiproj.slider.board.Board;

import java.util.List;
import java.util.Random;

/**
 * Created by hao on 15/5/17.
 */
public class AgentMonteCarlo extends Agent {

    /* Used for random move agent */
    private Random rng;
    private Move move;

    private double x = 100;
    private double wins = 0;

    private List<Move> monteMoves;
    private List<Move> hMoves;
    private List<Move> vMoves;

    private Board origBoard;
    private Board tempBoard;

    private Move bestMove;
    private double bestScore;

    public AgentMonteCarlo() {

    }

    public void init(int d, Board b, char p) {
        this.dimension = d;
        this.player = p;

        /* reads in and generates player's internal board */
        this.gameBoard = b;
    }

    public Move getMove(char player, Board b) {
        this.tempBoard = new Board(this.dimension, b.toString().split("\n"));
        bestMove = null;    // what's being returned
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
                        System.out.println("##### H TURN #####");
                        if (first && player == 'H') {
                            tempBoard.update(tempBoard, m);
                            System.out.println(tempBoard.getPlayerHLocations());
                            tempBoard.printB(tempBoard);
                            first = false;
                        } else {
                            // simulation
                            hMoves = generateMoves('H', tempBoard);
                            System.out.println(hMoves);
                            System.out.println(tempBoard.getPlayerHLocations());

                            // check if no moves possible
                            if (hMoves.size() == 0) {
                                if (numLegalMoves('H', tempBoard) == 0) {
                                    hTurn = false;
                                    continue;   // pass
                                }
                            }

                            move = hMoves.get(rng.nextInt(hMoves.size()));
                            System.out.println(move);

                            tempBoard.update(tempBoard, move);
                            System.out.println(tempBoard.getPlayerHLocations());
                            tempBoard.printB(tempBoard);
                        }

                        if (tempBoard.getPlayerHLocations().size() == 0) { // no more remaining, game over
                            if (player == 'H') {
                                wins += 1;
                            }
                            System.out.println("Here H");
                            simulate = false;
                        }

                        hTurn = false;
                    } else {
                        System.out.println("##### V TURN #####");
                        if (first && player == 'V') {
                            tempBoard.update(tempBoard, m);
                            System.out.println(tempBoard.getPlayerVLocations());
                            tempBoard.printB(tempBoard);
                            first = false;
                        } else {
                            vMoves = generateMoves('V', tempBoard);
                            System.out.println(vMoves);
                            System.out.println(tempBoard.getPlayerVLocations());

                            // check if no moves possible
                            if (vMoves.size() == 0) {
                                if (numLegalMoves('V', tempBoard) == 0) {
                                    hTurn = true;
                                    continue;   // pass
                                }
                            }

                            move = vMoves.get(rng.nextInt(vMoves.size()));
                            System.out.println(move);

                            tempBoard.update(tempBoard, move);
                            System.out.println(tempBoard.getPlayerVLocations());
                            tempBoard.printB(tempBoard);

                        }

                        if (tempBoard.getPlayerVLocations().size() == 0) { // no more, game over
                            if (player == 'V') {
                                wins += 1;
                            }
                            System.out.println("Here H");
                            simulate = false;
                        }

                        hTurn = true;
                    }
                }
                System.out.println(tempBoard);
                System.out.println("GAME " + i + " END, WINS: " + wins);
                this.tempBoard = new Board(this.dimension, b.toString().split("\n"));
                System.out.println(tempBoard);
                if (player == 'H') {
                    hTurn = true;
                } else {
                    hTurn = false;
                }
            }

            if (wins/x > bestScore) {
                System.out.print(bestScore);
                System.out.println(wins/x);

                bestScore = (wins/x);
                bestMove = m;
            }

            System.out.println(m + " STATS" + wins + "/" + x);
            wins = 0;

        }


        System.out.println("ALL OVER");

        System.out.println(bestMove);
//        System.exit(1);

        return bestMove;
    }

}
