/* Hao Le - leh2
 * Sam Chung - chungs1
 * 
 * MoveManager Class
 * Used to carry the object of a move with a score
 * Last Modified: 17/05/17
 *
 */

package aiproj.slider.agents.helper;

import aiproj.slider.Move;

public class MoveManager {

    private Move move;
    double score;

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public MoveManager(Move m, double s) {
        this.move = m;
        this.score = s;
    }

    @Override
    public String toString() {
        return "(Move: " + move + ", score: " + score + ")";
    }

}
