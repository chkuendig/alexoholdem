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

        int offset    = 0;
        int pairIndex = flop.holeA().ordinal();

        if (flopCase.equals(FlopCase.CASE_12_113))
            return offset + indexOf_12_113( flop ) * 13 + pairIndex;
        offset += COUNT_12_113;

        if (flopCase.equals(FlopCase.CASE_12_133))
            return offset + indexOf_12_133( flop ) * 13 + pairIndex;
        offset += COUNT_12_133;

        if (flopCase.equals(FlopCase.CASE_WW_2WW))
            return offset + indexOf_ww_2ww( flop ) * 13 + pairIndex;
        offset += COUNT_WW_2WW;

        if (flopCase.equals(FlopCase.CASE_12_123))
            return offset + indexOf_12_123( flop ) * 13 + pairIndex;
        offset += COUNT_12_123;

        if (flopCase.equals(FlopCase.CASE_12_134))
            return offset + indexOf_12_134( flop ) * 13 + pairIndex;
        offset += COUNT_12_134;

        if (flopCase.equals(FlopCase.CASE_12_1WW))
            return offset + indexOf_12_1ww( flop ) * 13 + pairIndex;
        offset += COUNT_12_1WW;

        if (flopCase.equals(FlopCase.CASE_WW_223))
            return offset + indexOf_ww_223( flop ) * 13 + pairIndex;
        offset += COUNT_WW_223;

        if (flopCase.equals(FlopCase.CASE_WW_222))
            return offset + indexOf_ww_222( flop ) * 13 + pairIndex;
        offset += COUNT_WW_222;

        if (flopCase.equals(FlopCase.CASE_12_111))
            return offset + indexOf_12_111( flop ) * 13 + pairIndex;
        offset += COUNT_12_111;

        if (flopCase.equals(FlopCase.CASE_12_112))
            return offset + indexOf_12_112( flop ) * 13 + pairIndex;
        offset += COUNT_12_112;
        
        return -1;
    }


    //--------------------------------------------------------------------
    // [ONE, TWO][ONE, ONE, TWO] :: 10296
    // 0 .. 791 X 13 = 0 .. 10295
    private static final int COUNT_12_112 = 10296;
    private int indexOf_12_112(IsoFlop flop)
    {
        int oneOffsetA = (flop.holeA().ordinal() <
                          flop.flopA().ordinal()) ? -1 : 0;
        int oneOffsetB = (flop.holeA().ordinal() <
                          flop.flopB().ordinal()) ? -1 : 0;
        int twoOffset  = (flop.holeA().ordinal() <
                          flop.flopC().ordinal()) ? -1 : 0;

        int oneA = flop.flopA().ordinal() + oneOffsetA;
        int oneB = flop.flopB().ordinal() + oneOffsetB;
        int two  = flop.flopC().ordinal() + twoOffset;

        return colex(oneA, oneB) * 12 + two;
    }


    //--------------------------------------------------------------------
    // [ONE, TWO][ONE, ONE, ONE] :: 2860
    // 0 .. 219 X 13 = 0 .. 2859
    private static final int COUNT_12_111 = 2860;
    private int indexOf_12_111(IsoFlop flop)
    {
        int offsetA = (flop.holeA().ordinal() <
                       flop.flopA().ordinal()) ? -1 : 0;
        int offsetB = (flop.holeA().ordinal() <
                       flop.flopB().ordinal()) ? -1 : 0;
        int offsetC = (flop.holeA().ordinal() <
                       flop.flopC().ordinal()) ? -1 : 0;

        int oneA = flop.flopA().ordinal() + offsetA;
        int oneB = flop.flopB().ordinal() + offsetB;
        int oneC = flop.flopC().ordinal() + offsetC;

        return colex(oneA, oneB, oneC);
    }


    //--------------------------------------------------------------------
    // [WILD, WILD][TWO, TWO, TWO] :: 3718
    // 0 .. 285 X 13 = 0 .. 3717
    private static final int COUNT_WW_222 = 3718;
    private int indexOf_ww_222(IsoFlop flop)
    {
        int twoA = flop.flopA().ordinal();
        int twoB = flop.flopB().ordinal();
        int twoC = flop.flopC().ordinal();

        return colex(twoA, twoB, twoC);
    }


    //--------------------------------------------------------------------
    //[WILD, WILD][TWO, TWO, THREE] :: 13182
    // 0 .. 1013 X 13 = 0 .. 13181
    private static final int COUNT_WW_223 = 13182;
    private int indexOf_ww_223(IsoFlop flop)
    {
        int twoA  = flop.flopA().ordinal();
        int twoB  = flop.flopB().ordinal();
        int three = flop.flopC().ordinal();

        return colex(Math.min(twoA, twoB),
                     Math.max(twoA, twoB)) * 13 + three;
    }


    //--------------------------------------------------------------------
    // [ONE, TWO][ONE, WILD, WILD] :: 2028
    // 0 .. 155 X 13 = 0 .. 2027
    private static final int COUNT_12_1WW = 2028;
    private int indexOf_12_1ww(IsoFlop flop)
    {
        int  oneOffset = (flop.holeA().ordinal() <
                          flop.flopA().ordinal()) ? -1 : 0;
        int  oneIndex = flop.flopA().ordinal() +  oneOffset;
        int wildIndex = flop.flopB().ordinal();

        return oneIndex * 13 + wildIndex;
    }


    //--------------------------------------------------------------------
    // [ONE, TWO][ONE, THREE, FOUR] :: 12168
    // 0 .. 935 X 13 = 0 .. 12167
    private static final int COUNT_12_134 = 12168;
    private int indexOf_12_134(IsoFlop flop)
    {
        int oneOffset = (flop.holeA().ordinal() <
                         flop.flopA().ordinal()) ? -1 : 0;

        int   oneIndex = flop.flopA().ordinal() + oneOffset;
        int threeIndex = flop.flopB().ordinal();
        int  fourIndex = flop.flopC().ordinal();

        //System.out.println(flop);
        return colex(Math.min(threeIndex, fourIndex),
                     Math.max(threeIndex, fourIndex)) * 12 + oneIndex;
    }


    //--------------------------------------------------------------------
    // [ONE, TWO][ONE, TWO, THREE] :: 11154
    // 0 .. 857 X 13 = 0 .. 11153
    private static final int COUNT_12_123 = 11154;
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
    private static final int COUNT_WW_2WW = 2028;
    private int indexOf_ww_2ww(IsoFlop flop)
    {
        int wildOffset = (flop.holeA().ordinal() <
                          flop.flopB().ordinal()) ? -1 : 0;
        int wildIndex = flop.flopB().ordinal() + wildOffset;
        int twoIndex  = flop.flopA().ordinal();
        return wildIndex * 13 + twoIndex;
    }


    //--------------------------------------------------------------------
    // [ONE, TWO][ONE, THREE, THREE] :: 12168
    // 0 .. 935 X 13 = 0 .. 12167
    private static final int COUNT_12_133 = 12168;
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
    // [ONE, TWO][ONE, ONE, THREE] :: 11154
    // 0 .. 857 X 13 = 0 .. 11153
    private static final int COUNT_12_113 = 11154;
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
