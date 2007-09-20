package ao.hand_eval.eval5;

import ao.util.persist.PersistentShorts;

/**
 * see
 *  http://www.suffecool.net/poker/code/arrays.h
 *  http://www.geocities.com/psenzee/code/fast_eval.c
 */
public class Eval5Lookup
{
    //--------------------------------------------------------------------
    private Eval5Lookup() {}


    //--------------------------------------------------------------------
    private final static short flushes[];
    private final static short hashAdjust[];
    private final static short hashVals[];
    private final static short unique5[];

    static
    {
        unique5    = PersistentShorts.asArray("lookup/unique5.txt");
        flushes    = PersistentShorts.asArray("lookup/flushes.txt");
        hashAdjust = PersistentShorts.asArray("lookup/hash_adjust.txt");
        hashVals   = PersistentShorts.asArray("lookup/hash_vals.txt");
    }


    //--------------------------------------------------------------------
    public static short unique5(int index)
    {
        return unique5[ index ];
    }

    
    //--------------------------------------------------------------------
    public static short flushes(int index)
    {
        return flushes[ index ];
    }

    //--------------------------------------------------------------------
    public static short remainingHands(int primeProduct)
    {
        return hashVals[ findFast(primeProduct) ];
    }
    private static int findFast(int u)
    {
        long adjusted = u;
        adjusted = (adjusted + 0xe91aaa35) & 0xFFFFFFFFL;

        adjusted ^= adjusted >> 16;

        adjusted += ((adjusted << 8) & 0xFFFFFFFFL);
        adjusted &= 0xFFFFFFFFL;

        adjusted ^= adjusted >> 4;

        long b  = (adjusted >> 8) & 0x1ff;
        long a  = (((adjusted +
                     ((adjusted << 2) & 0xFFFFFFFFL)
                    ) & 0xFFFFFFFFL
                   ) >> 19);
        return (int)(a ^ hashAdjust[(int) b]);
    }
}
