package ao.holdem.engine.agglom.hist;

import ao.Infrastructure;
import ao.holdem.model.card.Card;
import ao.holdem.engine.eval.eval5.Eval5;
import ao.holdem.engine.eval.eval7.Eval7Faster;
import ao.util.data.primitive.ShortList;
import ao.util.io.Dirs;
import ao.util.math.stats.Combiner;
import ao.util.persist.PersistentShorts;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.BitSet;

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
    private static final Logger LOG =
            Logger.getLogger(CompactRiverStrengths.class);

    private CompactRiverStrengths() {}


    //--------------------------------------------------------------------
    private final static File DIR         = Dirs.get(Infrastructure.path(
            "lookup/eval/river/compact"));
    private final static File MAP_STORE   =
            new File(DIR, "compact_strength_map.short");
//    private final static File COUNT_STORE =
//            new File(DIR, "compact_strength_count.short");
//            new File(DIR, "sorted_unused_rivers.short");


//    private final static short[] MAP   = computeMap();
//    public  final static short   COUNT = 4824;

    private final static short[] MAP;
    public  final static short   COUNT;

    static
    {
        MAP = retrieveOrComputeCompactStrengths();

        short maxIndex = -1;
        for (int i = MAP.length - 1; i >= 0; i--) {
            if (MAP[i] != -1) {
                maxIndex = MAP[i];
                break;
            }
        }
        COUNT = (short)(maxIndex + 1);
    }


    //--------------------------------------------------------------------
    public static void main(String[] args) {
        LOG.info("Total used river hand strengths: " + COUNT);
    }

    
    //--------------------------------------------------------------------
    private static short[] retrieveOrComputeCompactStrengths()
    {
        short[] compactStrengths =
                PersistentShorts.retrieve(MAP_STORE);
        if (compactStrengths != null) return compactStrengths;

        compactStrengths = computeMap(
                computeSortedUnusedRivers());
        PersistentShorts.persist(compactStrengths, MAP_STORE);

        return compactStrengths;
    }


    //--------------------------------------------------------------------
    private static short[] computeSortedUnusedRivers()
    {
        BitSet unUsed = new BitSet(Eval5.VALUE_COUNT);
        unUsed.set(0, Eval5.VALUE_COUNT);

        for (Card[] hand : new Combiner<Card>(Card.VALUES, 7)) {
            unUsed.clear( Eval7Faster.valueOf(hand) );
        }

        ShortList compactUnused = new ShortList();
        for (int i = unUsed.nextSetBit(0);
                 i >= 0;
                 i = unUsed.nextSetBit(i+1)) {
            compactUnused.add((short) i);
        }

        return compactUnused.toShortArray();
    }


    //--------------------------------------------------------------------
    private static short[] computeMap(short[] unusedRivers)
    {
//        short[] unusedRivers =
//                PersistentShorts.asArray(STORE);

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
