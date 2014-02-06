package ao.bucket.index.detail.river;

import ao.bucket.index.canon.flop.Flop;
import ao.bucket.index.canon.hole.CanonHole;
import ao.bucket.index.canon.river.River;
import ao.bucket.index.canon.turn.Turn;
import ao.bucket.index.detail.flop.FlopDetailFlyweight.CanonFlopDetail;
import ao.bucket.index.detail.flop.FlopDetails;
import ao.bucket.index.detail.turn.TurnRivers;
import ao.bucket.index.enumeration.HandEnum;
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
