package ao.holdem.abs.calc;

import ao.holdem.canon.flop.Flop;
import ao.holdem.model.card.canon.hole.CanonHole;
import ao.holdem.canon.river.River;
import ao.holdem.canon.turn.Turn;
import ao.holdem.canon.enumeration.HandEnum;
import ao.holdem.canon.enumeration.PermisiveFilter;
import ao.holdem.canon.enumeration.UniqueFilter;
import ao.holdem.model.card.Community;
import ao.holdem.abs.odds.Odds;
import ao.holdem.abs.odds.agglom.hist.GeneralHistFinder;
import ao.holdem.abs.odds.agglom.impl.PreciseHeadsUpOdds;
import ao.util.pass.Traverser;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Date: Aug 21, 2008
 * Time: 7:05:20 PM
 */
public class CanonIndexTest
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(CanonIndexTest.class);

    public static void main(String[] args)
    {
        testFill();
        testConsistency();
    }


    //--------------------------------------------------------------------
    public static void testConsistency()
    {
        testHoleConsistency();
        testFlopConsistency();
        testTurnConsistency();
        try {
            RiverEvalLookupTest.main(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testTurnConsistency()
    {
        LOG.debug("testTurnConsistency");

        final int[]  count = {0};
        final int[] turns =
                new int[ Turn.CANONS ];
        HandEnum.turns(
                new PermisiveFilter<CanonHole>(),
                new PermisiveFilter<Flop>(),
                new PermisiveFilter<Turn>(),
                new Traverser<Turn>() {
                    public void traverse(Turn turn) {
                        int index = turn.canonIndex();

                        int hist =
                                GeneralHistFinder.INSTANCE
                                        .compute(turn).hashCode();
                        if (turns[ index ] == 0) {
                            turns[ index ] = hist;
                        } else if (turns[ index ] != hist) {
                            LOG.error("inconsistent turn " +
                                        turn.canonIndex());
                        }

                        checkpoint(count[0]++);
                    }
                });
    }

    private static void testFlopConsistency()
    {
        LOG.debug("testFlopConsistency");

        final int[] count = {0};
        final int[] flops =
                new int[ Flop.CANONS ];
        HandEnum.flops(
                new PermisiveFilter<CanonHole>(),
                new PermisiveFilter<Flop>(),
                new Traverser<Flop>() {
                    public void traverse(Flop flop) {
                        int index = flop.canonIndex();

                        int hist =
                                GeneralHistFinder.INSTANCE
                                        .compute(flop).hashCode();
                        if (flops[ index ] == 0) {
                            flops[ index ] = hist;
                        } else if (flops[ index ] != hist) {
                            LOG.error("inconsistent flop " +
                                        flop.canonIndex());
                        }

                        checkpoint(count[0]++);
                    }
                });
    }

    private static void testHoleConsistency()
    {
        LOG.debug("testHoleConsistency");

        final Odds[] holes = new Odds[ CanonHole.CANONS ];
        HandEnum.holes(
                new PermisiveFilter<CanonHole>(),
                new Traverser<CanonHole>() {
                    public void traverse(CanonHole canonHole) {
                        int index = canonHole.canonIndex();

                        Odds odds = PreciseHeadsUpOdds.INSTANCE.compute(
                                            canonHole.reify(),
                                            Community.PREFLOP);
                        if (holes[ index ] == null) {
                            holes[ index ] = odds;        
                        } else if (! holes[ index ].equals(odds)) {
                            LOG.error("inconsistent hole " +
                                        canonHole.canonIndex());
                        }
                    }
                });
    }


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public static void testFill()
    {
        final UniqueFilter  holeFilter = new UniqueFilter("%1$s");
        final UniqueFilter  flopFilter = new UniqueFilter();
        final UniqueFilter  turnFilter = new UniqueFilter();
        final UniqueFilter riverFilter = new UniqueFilter();

        HandEnum.rivers(
                holeFilter, flopFilter, turnFilter, riverFilter,
                new Traverser<River>() {
                    public void traverse(River river) {}
                });

        LOG.info("Hole Gapper Status:");
        holeFilter.gapper().displayStatus();

        LOG.info("Flop Gapper Status:");
        flopFilter.gapper().displayStatus();

        LOG.info("Turn Gapper Status:");
        turnFilter.gapper().displayStatus();

        LOG.info("River Gapper Status:");
        riverFilter.gapper().displayStatus();
    }


    //--------------------------------------------------------------------
    private static void checkpoint(int count) {
        if ( count      % (     100 * 1000) == 0) System.out.print(".");
        if ((count + 1) % (50 * 100 * 1000) == 0) System.out.println();
    }
}
