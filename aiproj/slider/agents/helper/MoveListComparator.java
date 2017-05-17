/* Hao Le - leh2
 * Sam Chung - chungs1
 * 
 * MoveListComparator Class
 * Compares the moves which are available to player
 * Used for sorting the moves
 * Last Modified: 17/05/17
 *
 */

package aiproj.slider.agents.helper;
import aiproj.slider.Move;

import java.util.Comparator;
import java.lang.Math;

public class MoveListComparator{

    public static Comparator<Move> HComparator = new Comparator<Move>(){

        public int compare (Move move1, Move move2){
            // if move1 is headed right
            if(move1.d == Move.Direction.RIGHT){
                if(move2.d != Move.Direction.RIGHT){
                    return -1;
                }
            }

            // if move1 is headed up
            if (move1.d == Move.Direction.UP){
                if (move2.d == Move.Direction.RIGHT){
                    return 1;
                }
                else if (move2.d != Move.Direction.UP){
                    return -1;
                }
            }

            // if move2 is headed up or right
            if (move2.d == Move.Direction.UP || move2.d == Move.Direction.RIGHT){
                return 1;
            }
            
            // neither is headed right or up, check the distance.
            if(Distance(move1.i,move1.j) < Distance(move2.i, move2.j)){
                return -1;
            }
            else{
                return 1;
            }
        }

    };
    public static Comparator<Move> VComparator = new Comparator<Move>(){

        public int compare (Move move1, Move move2){
            // if move1 is headed up
            if(move1.d == Move.Direction.UP){
                if(move2.d != Move.Direction.UP){
                    return -1;
                }
            }
            
            //if move1 is headed right
            if (move1.d == Move.Direction.RIGHT){
                if (move2.d == Move.Direction.UP){
                    return 1;
                }
                else if (move2.d != Move.Direction.RIGHT){
                    return -1;
                }
            }
            
            //if move2 is headed up or right
            if (move2.d == Move.Direction.UP || move2.d == Move.Direction.RIGHT){
                return 1;
            }
            
            // neither is headed right or up, check the distance.
            if(Distance(move1.i,move1.j) < Distance(move2.i, move2.j)){
                return -1;
            }
            else{
                return 1;
            }
        }

    };
    /**
     * Returns distance from (0,0)
     * @param x X cordinate, @param y Y coordinate
     * @return distance
     */
    private static double Distance(int x, int y){
        return Math.sqrt(x*x+y*y);
    }

}