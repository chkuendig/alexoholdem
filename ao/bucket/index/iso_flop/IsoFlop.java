package ao.bucket.index.iso_flop;

import ao.bucket.index.iso_cards.FastOrder;
import ao.bucket.index.iso_cards.wild.card.FastWildCard;
import ao.bucket.index.iso_turn.IsoTurn;
import static ao.bucket.index.iso_util.IsoCaseUtils.distinct;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.Suit;

import java.util.Arrays;

/**
 * Date: Jun 18, 2008
 * Time: 2:57:55 PM
 */
public class IsoFlop
{
    //--------------------------------------------------------------------
    private final boolean      IS_HOLE_PAIR;
    private final FastWildCard HOLE[];
    private final FastWildCard FLOP[];
    private final FastOrder    ORDER;
    private final FlopCase     FLOP_CASE;


    //--------------------------------------------------------------------
    public IsoFlop(Hole hole,
                   Card flopA,
                   Card flopB,
                   Card flopC)
    {
        ORDER        = hole.order().refine(
                            orderSuitsBy(flopA, flopB, flopC));
        HOLE         = hole.asWild(ORDER);
        IS_HOLE_PAIR = hole.paired();

        FLOP = new FastWildCard[]{
                ORDER.asWild(flopA),
                ORDER.asWild(flopB),
                ORDER.asWild(flopC)};
        Arrays.sort(FLOP);

        FLOP_CASE = computeFlopCase();
    }


    //--------------------------------------------------------------------
    public FlopCase flopCase()
    {
        return FLOP_CASE;
    }
    private FlopCase computeFlopCase()
    {
        return FlopCase.newInstance(
                IS_HOLE_PAIR,
                HOLE[0].suit(), HOLE[1].suit(),
                FLOP[0].suit(), FLOP[1].suit(), FLOP[2].suit());
    }

    public int subIndex()
    {
        return FLOP_CASE.subIndex(
                HOLE[0].rank().ordinal(), HOLE[1].rank().ordinal(),
                FLOP[0].rank().ordinal(), FLOP[1].rank().ordinal(),
                FLOP[2].rank().ordinal());
    }


    //--------------------------------------------------------------------
    public IsoTurn isoTurn(Card hole[],
                           Card flop[],
                           Card turnCard)
    {
//        return new IsoTurn(ORDER, hole, flop, turnCard);
        return null;
    }

    public FastOrder order()
    {
        return ORDER;
    }

    
    //--------------------------------------------------------------------
    private static FastOrder orderSuitsBy(
            Card flopA, Card flopB, Card flopC)
    {
        // sort by rank
        //if (Card.BY_RANK_DSC.compare(flopA, flopB) > 0)
        if (flopA.ordinal() < flopB.ordinal())
        {
            Card temp = flopA;
            flopA     = flopB;
            flopB     = temp;
        }
        if (flopB.ordinal() < flopC.ordinal())
        {
            Card temp = flopB;
            flopB     = flopC;
            flopC     = temp;
        }
        if (flopA.ordinal() < flopB.ordinal())
        {
            Card temp = flopA;
            flopA     = flopB;
            flopB     = temp;
        }

        Suit sA = flopA.suit(),
             sB = flopB.suit(),
             sC = flopC.suit();

        int distinctRanks =
                distinct(flopA.rank(), flopB.rank(), flopC.rank());
        int distinctSuits = distinct(sA, sB, sC);

        if (distinctRanks == 1)
        {
            return FastOrder.triplet(sA, sB, sC);
        }
        else if (distinctRanks == 2)
        {
            if (distinctSuits == 2)
            {
                return fastSuitedPlus(sA, sB, sC);
            }
            else
            {
                return flopA.rank() == flopB.rank()
                       ? FastOrder.partSuited(sA, sB, sC)
                       : flopA.rank() == flopC.rank()
                         ? FastOrder.partSuited(sA, sC, sB)
                         : FastOrder.partSuited(sB, sC, sA);
            }
        }
        else
        {
            if (distinctSuits == 1)
            {
                return FastOrder.suited(sA);
            }
            else if (distinctSuits == 2)
            {
                return fastSuitedPlus(sA, sB, sC);
            }
            else
            {
                return FastOrder.ordered(sA, sB, sC);
            }
        }
    }

    private static FastOrder fastSuitedPlus(Suit a, Suit b, Suit c)
    {
        return a == b
               ? FastOrder.partSuited(a, c)
               : a == c
                 ? FastOrder.partSuited(a, b)
                 : FastOrder.partSuited(b, a);
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return  IS_HOLE_PAIR          +
                Arrays.toString(HOLE) +
                Arrays.toString(FLOP);
    }


    //--------------------------------------------------------------------
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IsoFlop isoFlop = (IsoFlop) o;
        return
               IS_HOLE_PAIR == isoFlop.IS_HOLE_PAIR &&

               HOLE[0] == isoFlop.HOLE[0] &&
               HOLE[1] == isoFlop.HOLE[1] &&

               FLOP[0] == isoFlop.FLOP[0] &&
               FLOP[1] == isoFlop.FLOP[1] &&
               FLOP[2] == isoFlop.FLOP[2];
    }

    @Override
    public int hashCode()
    {
        int result = IS_HOLE_PAIR ? 1231 : 1237;

        result = 31 * result + HOLE[0].hashCode();
        result = 31 * result + HOLE[1].hashCode();

        result = 31 * result + FLOP[0].hashCode();
        result = 31 * result + FLOP[1].hashCode();
        result = 31 * result + FLOP[2].hashCode();

        return result;
    }
}
