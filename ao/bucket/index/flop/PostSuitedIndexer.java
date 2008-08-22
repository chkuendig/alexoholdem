package ao.bucket.index.flop;

import ao.bucket.index.iso_cards.IsoFlop;
import ao.bucket.index.iso_cards.IsoHole;
import ao.bucket.index.iso_case.FlopCase;
import static ao.util.stats.Combo.colex;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by IntelliJ IDEA.
 * User: iscott
 * Date: Aug 14, 2008
 * Time: 3:12:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class PostSuitedIndexer
{
    //--------------------------------------------------------------------
    public int indexOf(
            IsoHole  hole,
            FlopCase flopCase, IsoFlop flop)
    {
        assert flop.holeA() != flop.holeB();

        if (flopCase.equals(FlopCase.CASE_11_122))
            return indexOf_11_122(flop);

        //hole.holeCase().index()


        return -1;
    }


    //--------------------------------------------------------------------
    // [ONE, ONE][ONE, TWO, TWO] :: 66924
    // 0 .. 5147 X 13 = 0 .. 66923
    private static final int COUNT_11_122 = 66924;
    private int indexOf_11_122(IsoFlop flop)
    {
        int offsetOne = (flop.holeA().ordinal() <
                         flop.flopA().ordinal()) ? -1 : 0;
        if (flop.holeB().ordinal() <
            flop.flopA().ordinal()) offsetOne--;

        int indexOne  = flop.flopA().ordinal() + offsetOne;

        int indexTwoA = flop.flopB().ordinal();
        int indexTwoB = flop.flopC().ordinal();

//        System.out.println(flop);
        return colex(min(indexTwoA, indexTwoB),
                     max(indexTwoA, indexTwoB)) * 11 + indexOne;
    }
}
