package aiproj.slider;

import aiproj.slider.Move;

/**
 * Created by hao on 12/5/17.
 */
public class MoveManager {

    private Move move;
    private int score;

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public MoveManager(Move m, int s) {
        this.move = m;
        this.score = s;
    }

    @Override
    public String toString() {
        return "(Move: " + move + ", score: " + score + ")";
    }

}
