package ao.bucket.index.post_flop.river;

import ao.bucket.index.card.CanonCard;
import ao.bucket.index.card.CanonSuit;
import ao.bucket.index.card.Order;
import ao.bucket.index.post_flop.turn.Turn;
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
    private final int       RANK_INDEX;


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
        RANK_INDEX       = RIVER.rank().ordinal() -
                            riverOffset(hole, flop, turn);
    }

    private int riverOffset(CanonCard hole[],
                            CanonCard flop[],
                            CanonCard turn)
    {
        return riverOffset(hole[0]) +
               riverOffset(hole[1]) +
               riverOffset(flop[0]) +
               riverOffset(flop[1]) +
               riverOffset(flop[1]) +
               riverOffset(turn);
    }
    private int riverOffset(CanonCard precedent)
    {
        return RIVER.suit() == precedent.suit() &&
                   RIVER.rank().comesAfter( precedent.rank() )
               ? 1 : 0;
    }


    //--------------------------------------------------------------------
    public CanonSuit riverSuit()
    {
        return RIVER.suit();
    }

    public int riverRankIndex()
    {
        return RANK_INDEX;
    }

    public long canonIndex()
    {
        return 0;
    }
}
