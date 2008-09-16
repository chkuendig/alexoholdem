package ao.bucket.index.river;

import ao.bucket.index.card.CanonCard;
import ao.bucket.index.card.Order;
import ao.bucket.index.turn.Turn;
import ao.holdem.model.card.Card;

/**
 * Date: Sep 1, 2008
 * Time: 8:36:13 PM
 */
public class CanonRiver
{
    //--------------------------------------------------------------------
//    private final CanonCard HOLE[], FLOP[], TURN, RIVER;
//    private final Order     ORDER;
    private final CanonCard RIVER;
//    private final int       RANK_INDEX;
    private final RiverCase CASE;

    private final int       PRECEDENCES;
    private final int       RANK_OFFSET;


    //--------------------------------------------------------------------
    public CanonRiver(
            Turn turnCards,
            Card riverCard)
    {
        Order order      = turnCards.refineOrder(
                               Order.suited(riverCard.suit()));
        CanonCard hole[] = turnCards.refineHole(order);
        CanonCard flop[] = turnCards.refineFlop(order);
        CanonCard turn   = turnCards.refineTurn(order);
        RIVER            = order.asCanon(riverCard);

        int precedencesAndOffset[] =
                initPrecedencesAndOffset(hole, flop, turn);
        PRECEDENCES = precedencesAndOffset[0];
        RANK_OFFSET = precedencesAndOffset[1];

        CASE = RiverCase.valueOf(
                RIVER.suit(), PRECEDENCES);
    }


    //--------------------------------------------------------------------
    private int[] initPrecedencesAndOffset(
            CanonCard hole[],
            CanonCard flop[],
            CanonCard turn)
    {
        int precedences = 0, offset = 0;
        if (RIVER.suit() == hole[0].suit()) {
            precedences++;
            if (RIVER.rank().comesAfter( hole[0].rank() )) offset++;
        }
        if (RIVER.suit() == hole[1].suit()) {
            precedences++;
            if (RIVER.rank().comesAfter( hole[1].rank() )) offset++;
        }
        if (RIVER.suit() == flop[0].suit()) {
            precedences++;
            if (RIVER.rank().comesAfter( flop[0].rank() )) offset++;
        }
        if (RIVER.suit() == flop[1].suit()) {
            precedences++;
            if (RIVER.rank().comesAfter( flop[1].rank() )) offset++;
        }
        if (RIVER.suit() == flop[2].suit()) {
            precedences++;
            if (RIVER.rank().comesAfter( flop[2].rank() )) offset++;
        }
        if (RIVER.suit() == turn.suit()) {
            precedences++;
            if (RIVER.rank().comesAfter( turn.rank() )) offset++;
        }
        return new int[]{precedences, offset};
    }
    

    //--------------------------------------------------------------------
//    private int riverOffset(CanonCard hole[],
//                            CanonCard flop[],
//                            CanonCard turn)
//    {
//        return riverOffset(hole[0]) +
//               riverOffset(hole[1]) +
//               riverOffset(flop[0]) +
//               riverOffset(flop[1]) +
//               riverOffset(flop[1]) +
//               riverOffset(turn);
//    }
//    private int riverOffset(CanonCard precedent)
//    {
//        return RIVER.suit() == precedent.suit() &&
//                   RIVER.rank().comesAfter( precedent.rank() )
//               ? 1 : 0;
//    }


    //--------------------------------------------------------------------
    public RiverCase riverCase()
    {
        return CASE;
    }
//    public CanonSuit riverSuit()
//    {
//        return RIVER.suit();
//    }

//    public int riverRankIndex()
//    {
//        return RANK_INDEX;
//    }

    public long canonIndex()
    {
//        RiverLookup.

        return 0;
    }
}
