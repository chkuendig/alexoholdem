package ao.holdem.engine.state.eval;

import ao.util.persist.PersistentShorts;

/**
 * See:
 *  http://www.suffecool.net/poker/code/arrays.h
 *  http://www.psenzee.com/code/fast_eval.c
 */
enum Eval5Lookup
{;
    //--------------------------------------------------------------------
    public static short unique5(int index)
    {
        return Eval5Const.UNIQUE_5[ index ];
    }

    
    //--------------------------------------------------------------------
    public static short flushes(int index)
    {
        return Eval5Const.FLUSHES[ index ];
    }
    

    //--------------------------------------------------------------------
    public static short remainingHands(int primeProduct)
    {
        return Eval5Const.HASH_VALUES[ findFast(primeProduct) ];
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
        return (int)(a ^ Eval5Const.HASH_ADJUST[(int) b]);
    }
}
