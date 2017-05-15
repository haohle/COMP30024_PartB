package aiproj.slider.agents.helper;


import aiproj.slider.board.Board;

import java.awt.Point;
import java.util.ArrayList;

/**
 * Created by SamCh on 15/05/2017.
 */
public class EvaluationFunctions {
    /**
     * Evaluates the positions of pieces on the board
     * @return heuristic value
     */
    public double evaluatePiecePos(char player, ArrayList<Point> pieces, int dimension){
        double score = 0;
        int x;
        int y;

        for(Point p: pieces){
            x = (int)p.getX();
            y = (int)p.getY();

            if (player == 'H'){
                //Player on the last column
                if (x == dimension-1){
                    score += x*2;
                }
                else{
                    score += (y+1) + (x+1)*2;
                }
            }

            if (player == 'V'){
                //Player on the last column
                if (y == dimension-1){
                    score += (y+1)*2;
                }
                else{
                    score += (x+1)+(y+1)*2;
                }
            }
        }
        score += (dimension-pieces.size())*10;
        return score;
    }
}
