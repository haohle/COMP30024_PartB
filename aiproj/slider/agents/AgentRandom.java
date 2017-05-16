package aiproj.slider.agents;

import java.util.Random;

import aiproj.slider.Move;

/**
 * Created by hao on 15/5/17.
 */
public class AgentRandom extends Agent {

//    /* Used for random move agent */
//    private Random rng;
    private Move move;

    public AgentRandom() {

    }

    @Override
    public Move move() {
        possMoves = generateMoves(this.player, this.gameBoard);

        /* no possible moves */
        if (possMoves.size() == 0) {
            return null;
        }

        move = possMoves.get(rng.nextInt(possMoves.size()));
        this.update(move);
        return move;
    }
}

