package ao.bucket.index.river;

import ao.bucket.index.card.CanonCard;
import ao.bucket.index.card.Order;
import ao.bucket.index.turn.Turn;
import ao.holdem.model.card.Card;

/**
 * Date: Sep 1, 2008
 * Time: 8:36:13 PM
 */
public class River
{
    //--------------------------------------------------------------------
//    private final CanonCard HOLE[], FLOP[], TURN, RIVER;
//    private final Order     ORDER;
    private final CanonCard RIVER;
    private final Card      RIVER_CARD;
    private final RiverCase CASE;
    private final Turn      TURN_CARDS;

//    private final int       PRECEDENCES;
    private final int       RANK_OFFSET;
//    private final long      CANON_INDEX;


    //--------------------------------------------------------------------
    public River(
            Turn turnCards,
            Card riverCard)
    {
        Order order      = turnCards.refineOrder(
                               Order.suited(riverCard.suit()));
        CanonCard hole[] = turnCards.refineHole(order);
        CanonCard flop[] = turnCards.refineFlop(hole, order);
        CanonCard turn   = turnCards.refineTurn(order);
        RIVER            = order.asCanon(riverCard);

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
            RANK_OFFSET = offset;
            CASE        = RiverCase.valueOf(
                            RIVER.suit(), precedences);
        }

        TURN_CARDS = turnCards;
        RIVER_CARD = riverCard;
    }


    //--------------------------------------------------------------------
    public RiverCase riverCase()
    {
        return CASE;
    }

    public CanonCard riverCard()
    {
        return RIVER;
    }

    public long canonIndex()
    {
        long globalOffset =
                RiverLookup.offset(TURN_CARDS.canonIndex());
        RiverCaseSet caseSet =
                RiverRawLookup.caseSet(TURN_CARDS.canonIndex());
        int caseOffset = caseSet.offsetOf( CASE );

        return globalOffset +
               caseOffset +
               (RIVER.rank().ordinal() - RANK_OFFSET);
    }


    //--------------------------------------------------------------------
    @Override
    public String toString() {
        return "River{" +
                "RIVER_CARD=" + RIVER_CARD +
                ", TURN_CARDS=" + TURN_CARDS +
                '}';
    }
}
