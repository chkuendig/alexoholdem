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
    private final Turn      TURN_CARDS;

    private final int       PRECEDENCES;
    private final int       RANK_OFFSET;
//    private final long      CANON_INDEX;


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
        TURN_CARDS = turnCards;
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
//        long globalOffset =
//                RiverSparceLookup.offset(turnCards.canonIndex());
        RiverCaseSet caseSet =
                RiverSparceLookup.caseSet(TURN_CARDS.canonIndex());
        int caseOffset = caseSet.offsetOf( CASE );

        return caseOffset +
               (RIVER.rank().ordinal() - RANK_OFFSET);

//        CANON_INDEX = //globalOffset +
//                      caseOffset +
//                      (RIVER.rank().ordinal() - RANK_OFFSET);

//        return CANON_INDEX;
    }
}
