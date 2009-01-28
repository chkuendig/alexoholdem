package ao.bucket.index.detail.river;

import ao.bucket.index.detail.flop.FlopDetailFlyweight.CanonFlopDetail;
import ao.bucket.index.detail.flop.FlopDetails;
import ao.bucket.index.detail.turn.TurnDetailFlyweight.CanonTurnDetail;
import ao.bucket.index.detail.turn.TurnDetails;
import ao.bucket.index.hole.CanonHole;
import ao.bucket.index.river.River;
import ao.bucket.index.turn.Turn;
import ao.holdem.model.card.Card;
import ao.odds.eval.eval7.Eval7Faster;

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
        CanonHole       hole = flop.hole();

        Turn turnSeq  = turn(hole, flop, turn);
        int  shortcut = shortcut(hole, flop, turn);

        Map<Long, CanonRiverDetail> details =
                new HashMap<Long, CanonRiverDetail>();
        for (Card r : remainder(hole, flop, turn))
        {
            River river = turnSeq.addRiver( r );

            try {
                river.canonIndex();
            } catch (Throwable t) {
                display(hole, flop, turn, r);
                t.printStackTrace();
                System.exit(1);
            }

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
            CanonHole hole, CanonFlopDetail flop, CanonTurnDetail turn)
    {
        return hole.addFlop(
                        flop.a(), flop.b(), flop.c()
                ).addTurn(turn.example());
    }

    private static Iterable<Card> remainder(
            CanonHole hole, CanonFlopDetail flop, CanonTurnDetail turn)
    {
        return EnumSet.complementOf(EnumSet.of(
                hole.a(), hole.b(),
                flop.a(), flop.b(), flop.c(),
                turn.example()));
    }

    private static int shortcut(
            CanonHole hole, CanonFlopDetail flop, CanonTurnDetail turn)
    {
        return Eval7Faster.shortcutFor(
                    hole.a(), hole.b(),
                    flop.a(), flop.b(), flop.c(),
                    turn.example());
    }
}
