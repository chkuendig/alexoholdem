package ao.odds.agglom.hist;

import ao.Infrastructure;
import ao.odds.eval.eval5.Eval5;
import ao.util.persist.PersistentShorts;

import java.util.Arrays;

/**
 * Date: Jan 30, 2009
 * Time: 2:21:52 PM
 *
 * Not all of the possible 7462 hand strengths appear on the river,
 *  this class captures that fact.
 */
public class CompactRiverStrengths
{
    //--------------------------------------------------------------------
    private static short[] MAP   = computeMap();
    public  static short   COUNT = 4824; // MAP[ MAP.length - 1 ] + 1;

    private CompactRiverStrengths() {}


    //--------------------------------------------------------------------
    private static short[] computeMap()
    {
        short[] unusedRivers =
                PersistentShorts.asArray(Infrastructure.path(
                        "lookup/eval/river/unused_rivers.txt"));

        short[] map = new short[ Eval5.VALUE_COUNT ];
        for (short i = 0, r = 0; i < map.length; i++)
        {
            boolean unused = (Arrays.binarySearch(unusedRivers, i) >= 0);

            if (unused) {
                map[ i ] = -1;
            } else {
                map[ i ] = r++;
            }
        }
        return map;
    }


    //--------------------------------------------------------------------
    public static short compact(short strength)
    {
        return MAP[ strength ];
    }
}
