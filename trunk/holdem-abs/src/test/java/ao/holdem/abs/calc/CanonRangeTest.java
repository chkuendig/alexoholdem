package ao.holdem.abs.calc;

import ao.holdem.abs.bucket.index.detail.range.CanonRange;
import ao.holdem.abs.bucket.index.detail.range.RangeLookup;
import ao.holdem.canon.flop.Flop;
import ao.holdem.model.card.canon.hole.CanonHole;
import ao.holdem.canon.turn.Turn;
import ao.holdem.model.Round;
import org.apache.log4j.Logger;

/**
 * Date: 1-Aug-2009
 * Time: 4:17:18 PM
 */
public class CanonRangeTest
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(CanonRangeTest.class);


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        testHoleContinuity();
        
        testTurnContinuity();

        testRiverContinuity();
    }


    //--------------------------------------------------------------------
    private static void testHoleContinuity()
    {
        LOG.debug("testHoleContinuity");

//        Progress   progress      = new Progress(FlopLookup.CANONS);
        CanonRange previousRange = null;
        for (int hole = 0; hole < CanonHole.CANONS; hole++)
        {
            CanonRange range =
                    RangeLookup.lookupRange(Round.PREFLOP, hole);

            if (previousRange != null)
            {
                if (previousRange.toInclusive() + 1 !=
                        range.from())
                {
                    LOG.error("hole->flop GAP AT " + hole);
                }
            }

            previousRange = range;
//            progress.checkpoint();
        }
    }


    //--------------------------------------------------------------------
    private static void testTurnContinuity()
    {
        LOG.debug("testTurnContinuity");

//        Progress   progress      = new Progress(FlopLookup.CANONS);
        CanonRange previousRange = null;
        for (int flop = 0; flop < Flop.CANONS; flop++)
        {
            CanonRange range =
                    RangeLookup.lookupRange(Round.FLOP, flop);

            if (previousRange != null)
            {
                if (previousRange.toInclusive() + 1 !=
                        range.from())
                {
                    LOG.error("flop->turn GAP AT " + flop);
                }
            }

            previousRange = range;
//            progress.checkpoint();
        }
    }


    //--------------------------------------------------------------------
    private static void testRiverContinuity()
    {
        LOG.debug("testRiverContinuity");

//        Progress   progress      = new Progress(TurnLookup.CANONS);
        CanonRange previousRange = null;
        for (int turn = 0; turn < Turn.CANONS; turn++)
        {
            CanonRange riverRange =
                    RangeLookup.lookupRange(Round.TURN, turn);

            if (previousRange != null)
            {
                if (previousRange.toInclusive() + 1 !=
                        riverRange.from())
                {
                    LOG.error("turn->river GAP AT " + turn);
                }
            }

            previousRange = riverRange;
//            progress.checkpoint();
        }
    }



}
