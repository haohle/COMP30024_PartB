package aiproj.slider.agents.helper;
import aiproj.slider.Move;

import java.util.Comparator;
import java.lang.Math;

/**
 * Created by SamCh on 14/05/2017.
 */
public class MoveListComparator implements Comparator<Move> {
    public int compare (Move move1, Move move2){
        //Preference pieces moving towards the top right corner.
        if(move1.d == Move.Direction.RIGHT || move1.d == Move.Direction.UP){
            if(move2.d == Move.Direction.RIGHT || move2.d == Move.Direction.UP){
                //Preference pieces which are closer to (0,0)
                if(Distance(move1.i,move1.j) < Distance(move2.i, move2.j)){
                    return -1;
                }
                else{
                    return 1;
                }
            }
            else{
                return -1;
            }
        }else{
            if(move2.d == Move.Direction.RIGHT || move2.d == Move.Direction.UP){
                return 1;
            }
            else{
                if(Distance(move1.i,move1.j) < Distance(move2.i, move2.j)){
                    return -1;
                }
                else{
                    return 1;
                }
            }
        }

    }
    /**
     * Returns distance from (0,0)
     * @param x X cordinate, @param y Y coordinate
     * @return distance
     */
    private double Distance(int x, int y){
        return Math.sqrt(x*x+y*y);
    }

}
