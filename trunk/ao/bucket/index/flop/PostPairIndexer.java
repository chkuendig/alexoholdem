package ao.bucket.index.flop;

import ao.bucket.index.iso_cards.IsoFlop;
import ao.bucket.index.iso_cards.IsoFlop.FlopCase;
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

        if (flopCase.equals(FlopCase.CASE_12_113))
        {
            return indexOf_12_113( flop );
        }
        else if (flopCase.equals(FlopCase.CASE_12_133))
        {
            return indexOf_12_133( flop );
        }
        else
        {
            return -1;
        }
    }


    //--------------------------------------------------------------------
    // [ONE, TWO][ONE, THREE, THREE]
    // 0 .. 12167
    private int indexOf_12_133(IsoFlop flop)
    {
        int oneOffset = (flop.holeA().ordinal() <
                            flop.flopA().ordinal()) ? -1 : 0;
        int oneIndex = flop.flopA().ordinal() + oneOffset;
        int threeIndex  = colex(flop.flopB().ordinal(),
                                flop.flopC().ordinal());
        int pairIndex  = flop.holeA().ordinal();

        return (  threeIndex * 12 +
                  oneIndex)* 13 +
                  pairIndex;
    }


    //--------------------------------------------------------------------
    // [ONE, TWO][ONE, ONE, THREE]
    // 0 .. 11153
    private int indexOf_12_113(IsoFlop flop)
    {
        int flopOffsetA = (flop.holeA().ordinal() <
                            flop.flopA().ordinal()) ? -1 : 0;
        int flopOffsetB = (flop.holeA().ordinal() <
                            flop.flopB().ordinal()) ? -1 : 0;

        int oneIndex  = colex(flop.flopA().ordinal() + flopOffsetA,
                              flop.flopB().ordinal() + flopOffsetB);
        int threeIndex = flop.flopC().ordinal();
        int pairIndex  = flop.holeA().ordinal();

        return (  oneIndex * 13 +
                  threeIndex)* 13 +
                  pairIndex;
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
