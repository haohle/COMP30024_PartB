/* Hao Le - leh2
 * Sam Chung - chungs1
 * 
 * AgentMonteCarlo
 * Simulates x number of games for the specified moves at the current board state
 * Returns the probability of that move winning the game if that move is actually made
 * Last Modified: 17/05/17
 *
 */

package aiproj.slider.agents;

import aiproj.slider.Move;
import aiproj.slider.board.Board;

import java.util.List;
import java.util.Random;

public class AgentMonteCarlo extends Agent {
    private double x = 100;     // number of games to simulate for each move
    private double wins = 0;    // number of wins with that move

    private List<Move> monteMoves;
    private List<Move> hMoves;
    private List<Move> vMoves;

    private Board origBoard;
    private Board tempBoard;

    private Move bestMove;
    private double bestScore;

    @Override
    public Move move() {
        origBoard = new Board(this.dimension, this.gameBoard.toString().split("\n"));
        Move tmpMonte = monteMove(this.player, origBoard);

        this.update(tmpMonte);
        return tmpMonte;
    }

    private Move monteMove(char player, Board b) {
        this.tempBoard = new Board(this.dimension, b.toString().split("\n"));
        
        bestMove = null;    // move being returned
        bestScore = 0;
        wins = 0;

        boolean hTurn;
        boolean simulate = true;
        boolean first = true;

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

                /* resets the board to the original state */
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

                if (bestScore == 0.9) {
                    wins = 0;
                    return bestMove;
                }
            }

            wins = 0;
        }

        return bestMove;
    }
}
