package ao.holdem.ai.bucket.index.detail.river;

import ao.holdem.model.canon.flop.Flop;
import ao.holdem.model.canon.hole.CanonHole;
import ao.holdem.model.canon.river.River;
import ao.holdem.model.canon.turn.Turn;
import ao.holdem.ai.bucket.index.detail.flop.FlopDetailFlyweight.CanonFlopDetail;
import ao.holdem.ai.bucket.index.detail.flop.FlopDetails;
import ao.holdem.ai.bucket.index.detail.turn.TurnRivers;
import ao.holdem.model.enumeration.HandEnum;
import ao.util.pass.Filter;
import ao.util.pass.Traverser;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: Jan 9, 2009
 * Time: 12:44:39 PM
 */
public class RiverExamples
{
    //--------------------------------------------------------------------
    public static List<River> examplesOf(long canonRiver)
    {
        final long acceptRiver = canonRiver;
        final int  acceptTurn = TurnRivers.turnFor( acceptRiver );
        CanonFlopDetail flopDetail = FlopDetails.containing(acceptTurn);
        final int  acceptFlop = (int) flopDetail.canonIndex();
        final int  acceptHole = (int) flopDetail.holeDetail().canonIndex();

        final List<River> examples = new ArrayList<River>();
        HandEnum.rivers(
                new Filter<CanonHole>() {
                    public boolean accept(CanonHole canonHole) {
                        return canonHole.canonIndex() == acceptHole;
                    }
                },
                new Filter<Flop>() {
                    public boolean accept(Flop flop) {
                        return flop.canonIndex() == acceptFlop;
                    }
                },
                new Filter<Turn>() {
                    public boolean accept(Turn turn) {
                        return turn.canonIndex() == acceptTurn;
                    }
                },
                new Filter<River>() {
                    public boolean accept(River river) {
                        return river.canonIndex() == acceptRiver;
                    }
                },
                new Traverser<River>() {
                    public void traverse(River river) {
                        examples.add( river );
                    }
                }
        );
        return examples;
    }
}
