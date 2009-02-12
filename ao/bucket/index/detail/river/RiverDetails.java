package ao.bucket.index.detail.river;

import ao.bucket.index.detail.flop.FlopDetailFlyweight.CanonFlopDetail;
import ao.bucket.index.detail.flop.FlopDetails;
import ao.bucket.index.detail.preflop.CanonHoleDetail;
import ao.bucket.index.detail.turn.TurnDetailFlyweight.CanonTurnDetail;
import ao.bucket.index.detail.turn.TurnDetails;
import ao.bucket.index.detail.turn.TurnRivers;
import ao.bucket.index.enumeration.HandEnum;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.hole.CanonHole;
import ao.bucket.index.river.River;
import ao.bucket.index.turn.Turn;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;
import ao.odds.eval.eval7.Eval7Faster;
import ao.util.misc.Filter;
import ao.util.misc.Traverser;

import java.util.*;

/**
 * Date: Jan 9, 2009
 * Time: 12:44:39 PM
 */
public class RiverDetails
{
    //--------------------------------------------------------------------
    public static Collection<CanonRiverDetail> lookup(int canonTurn)
    {
        CanonTurnDetail turn = TurnDetails.lookup( canonTurn );
        CanonFlopDetail flop =
                FlopDetails.containing( canonTurn );
        CanonHoleDetail hole = flop.holeDetail();

        Turn turnSeq  = turn(hole, flop, turn);
        int  shortcut = shortcut(hole, flop, turn);

        Map<Long, CanonRiverDetail> details =
                new HashMap<Long, CanonRiverDetail>();
        for (Card r : remainder(hole, flop, turn))
        {
            River river = turnSeq.addRiver( r );

            if (! details.containsKey( river.canonIndex() ))
            {
                details.put(
                        river.canonIndex(),
                        new CanonRiverDetail(
                                Eval7Faster.fastValueOf(shortcut, r),
                                river.canonIndex()));
            }
        }

        return details.values();
    }


    //--------------------------------------------------------------------
    private static void display(
            CanonHole hole, CanonFlopDetail flop, CanonTurnDetail turn,
            Card river)
    {
        System.out.println(Arrays.toString(new Card[]{
                hole.a(), hole.b(),
                flop.a(), flop.b(), flop.c(),
                turn.example(), river
        }));
    }

    private static Turn turn(
            CanonHoleDetail hole, CanonFlopDetail flop, CanonTurnDetail turn)
    {
        return hole.canon().addFlop(
                        flop.a(), flop.b(), flop.c()
                ).addTurn(turn.example());
    }

    private static Iterable<Card> remainder(
            CanonHoleDetail hole, CanonFlopDetail flop, CanonTurnDetail turn)
    {
        Hole concreteHole = hole.example();
        return EnumSet.complementOf(EnumSet.of(
                concreteHole.a(), concreteHole.b(),
                flop.a(), flop.b(), flop.c(),
                turn.example()));
    }

    private static int shortcut(
            CanonHoleDetail hole, CanonFlopDetail flop, CanonTurnDetail turn)
    {
        Hole concreteHole = hole.example();
        return Eval7Faster.shortcutFor(
                    concreteHole.a(), concreteHole.b(),
                    flop.a(), flop.b(), flop.c(),
                    turn.example());
    }


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
        });
        return examples;
    }
}
