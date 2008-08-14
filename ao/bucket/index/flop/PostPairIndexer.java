package ao.bucket.index.flop;

import ao.bucket.index.iso_cards.IsoFlop;
import ao.bucket.index.iso_case.FlopCase;
import ao.util.stats.Combo;

/**
 * Date: Aug 12, 2008
 * Time: 5:03:22 PM
 */
public class PostPairIndexer
{
    //--------------------------------------------------------------------
    public int indexOf(
            FlopCase flopCase, IsoFlop flop)
    {
        // on pair at hole
        assert flop.holeA() == flop.holeB();

        int   pairIndex = flop.holeA().ordinal();
        int inPairIndex =
                  flopCase.equals(FlopCase.CASE_12_113)
                ? indexOf_12_113( flop )
                : flopCase.equals(FlopCase.CASE_12_133)
                ? indexOf_12_133( flop )
                : flopCase.equals(FlopCase.CASE_WW_2WW)
                ? indexOf_ww_2ww( flop )
                : flopCase.equals(FlopCase.CASE_12_123)
                ? indexOf_12_123( flop )
                :-1;
        return inPairIndex * 13 + pairIndex;
    }

    
    //--------------------------------------------------------------------
    // [ONE, TWO][ONE, TWO, THREE] :: 11154
    // 0 .. 857 X 13 = 0 .. 11153
    private int indexOf_12_123(IsoFlop flop)
    {
        int oneOffset = (flop.holeA().ordinal() <
                         flop.flopA().ordinal()) ? -1 : 0;
        int twoOffset = ((flop.holeA().ordinal() <
                          flop.flopB().ordinal()) ? -1 : 0);

        int oneIndex = flop.flopA().ordinal() + oneOffset;
        int twoIndex = flop.flopB().ordinal() + twoOffset;
        int threeIndex = flop.flopC().ordinal();

        return colex(Math.min(oneIndex, twoIndex),
                     Math.max(oneIndex, twoIndex)) * 13 + threeIndex;
    }


    //--------------------------------------------------------------------
    // [WILD, WILD][TWO, WILD, WILD] :: 2028
    // [c,d][h][s]
    // 0 .. 155 X 13 = 0 .. 2027
    private int indexOf_ww_2ww(IsoFlop flop)
    {
        int wildOffset = (flop.holeA().ordinal() <
                          flop.flopB().ordinal()) ? -1 : 0;
        int wildIndex = flop.flopB().ordinal() + wildOffset;
        int oneIndex  = flop.flopA().ordinal();
        return wildIndex * 13 + oneIndex;
    }


    //--------------------------------------------------------------------
    // [ONE, TWO][ONE, THREE, THREE]
    // 0 .. 935 X 13 = 0 .. 12167
    private int indexOf_12_133(IsoFlop flop)
    {
        int oneOffset = (flop.holeA().ordinal() <
                            flop.flopA().ordinal()) ? -1 : 0;
        int oneIndex = flop.flopA().ordinal() + oneOffset;
        int threeIndex  = colex(flop.flopB().ordinal(),
                                flop.flopC().ordinal());
        return threeIndex * 12 + oneIndex;
    }


    //--------------------------------------------------------------------
    // [ONE, TWO][ONE, ONE, THREE]
    // 0 .. 857 X 13 = 0 .. 11153
    private int indexOf_12_113(IsoFlop flop)
    {
        int flopOffsetA = (flop.holeA().ordinal() <
                            flop.flopA().ordinal()) ? -1 : 0;
        int flopOffsetB = (flop.holeA().ordinal() <
                            flop.flopB().ordinal()) ? -1 : 0;
        int oneIndex  = colex(flop.flopA().ordinal() + flopOffsetA,
                              flop.flopB().ordinal() + flopOffsetB);
        int threeIndex = flop.flopC().ordinal();
        return oneIndex * 13 + threeIndex;
    }


    //--------------------------------------------------------------------
    private static int colex(int... set)
    {
        int colex = 0;
        for (int i = 0; i < set.length; i++)
        {
            colex += Combo.choose(set[i], i + 1);
        }
        return colex;
    }
}
