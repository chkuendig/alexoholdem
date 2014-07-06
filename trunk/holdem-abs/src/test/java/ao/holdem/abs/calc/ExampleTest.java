package ao.holdem.abs.calc;

import ao.holdem.abs.bucket.index.detail.flop.FlopDetailFlyweight;
import ao.holdem.abs.bucket.index.detail.flop.FlopDetails;
import ao.holdem.abs.bucket.index.detail.preflop.CanonHoleDetail;
import ao.holdem.abs.bucket.index.detail.preflop.HoleDetails;
import ao.holdem.abs.odds.Odds;
import ao.holdem.abs.odds.agglom.impl.PreciseHeadsUpOdds;
import ao.holdem.canon.enumeration.HandEnum;
import ao.holdem.canon.enumeration.PermisiveFilter;
import ao.holdem.canon.enumeration.UniqueFilter;
import ao.holdem.canon.flop.Flop;
import ao.holdem.model.card.canon.hole.CanonHole;
import ao.holdem.canon.turn.Turn;
import ao.holdem.model.card.Community;
import ao.util.pass.Traverser;

/**
 * 09/02/14 5:05 PM
 */
public class ExampleTest
{
    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        testHoleFlopConsistency();
        testHoleConsistency();
    }

    private static void testHoleFlopConsistency()
    {
        HandEnum.turns(
                new PermisiveFilter<CanonHole>("%1$s"),
                new PermisiveFilter<Flop>(),
                new UniqueFilter<Turn>(),
                new Traverser<Turn>() {
                    public void traverse(Turn turn) {
                        Odds givenOdds = PreciseHeadsUpOdds.INSTANCE.compute(
                                turn.flop().hole().reify(),
                                turn.flop().community());

//                CanonTurnDetail turnDet =
//                        TurnDetails.compact( turn.canonIndex() );
                        FlopDetailFlyweight.CanonFlopDetail flopDet =
                                FlopDetails.containing(
                                        turn.canonIndex());
                        CanonHoleDetail hole = flopDet.holeDetail();

                        Odds predictedOdds = PreciseHeadsUpOdds.INSTANCE.compute(
                                hole.example(),
                                new Community(flopDet.a(),
                                        flopDet.b(),
                                        flopDet.c()));

                        assert givenOdds.equals(predictedOdds);
                    }
                });
    }

    private static void testHoleConsistency()
    {
        HandEnum.flops(
                new PermisiveFilter<CanonHole>("%1$s"),
                new PermisiveFilter<Flop>(),
                new Traverser<Flop>() {
                    public void traverse(Flop flop) {

                        Odds givenOdds = PreciseHeadsUpOdds.INSTANCE.compute(
                                flop.hole().reify(), Community.PREFLOP);

                        Odds predictedOdds = PreciseHeadsUpOdds.INSTANCE.compute(
                                HoleDetails.lookup(
                                        (char) FlopDetails.lookup(
                                                flop.canonIndex()).holeDetail().canonIndex()
                                ).example(),
                                Community.PREFLOP);

                        assert givenOdds.equals( predictedOdds );
                    }});
    }
}
