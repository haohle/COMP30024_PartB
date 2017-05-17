/* Hao Le - leh2
 * Sam Chung - chungs1
 * 
 * AgentRandom
 * Randomly makes a move from the possible moves at the current state
 * Last Modified: 17/05/17
 *
 */

package aiproj.slider.agents;

import java.util.Random;

import aiproj.slider.Move;


public class AgentRandom extends Agent {
    @Override
    public Move move() {
        possMoves = generateMoves(this.player, this.gameBoard);

        /* no possible moves */
        if (possMoves.size() == 0) {
            return null;
        }

        /* select a move at random */
        move = possMoves.get(rng.nextInt(possMoves.size()));

        this.update(move);
        return move;
    }
}

