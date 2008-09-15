package ao.bucket.index.flop;

import ao.bucket.index.card.CanonCard;
import ao.bucket.index.card.Order;
import static ao.bucket.index.flop.FlopCanonUtils.distinct;
import ao.bucket.index.post_flop.turn.Turn;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.Suit;

import java.util.Arrays;

/**
 * Date: Jun 18, 2008
 * Time: 2:57:55 PM
 */
public class Flop
{
    //--------------------------------------------------------------------
    private final boolean   IS_HOLE_PAIR;
    private final CanonCard HOLE[];
    private final CanonCard FLOP[];
    private final Order     ORDER;
    private final FlopCase  FLOP_CASE;

    private final Hole      HOLE_CARDS;
    private final Card      FLOP_A;
    private final Card      FLOP_B;
    private final Card      FLOP_C;


    //--------------------------------------------------------------------
    public Flop(Hole hole,
                Card flopA,
                Card flopB,
                Card flopC)
    {
        ORDER        = hole.order().refine(
                            orderSuitsBy(flopA, flopB, flopC));
        HOLE         = hole.asWild(ORDER);
        IS_HOLE_PAIR = hole.paired();

        FLOP = new CanonCard[]{
                ORDER.asWild(flopA),
                ORDER.asWild(flopB),
                ORDER.asWild(flopC)};
        Arrays.sort(FLOP);

        FLOP_CASE = computeFlopCase();

        HOLE_CARDS = hole;
        FLOP_A     = flopA;
        FLOP_B     = flopB;
        FLOP_C     = flopC;
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
    public int canonIndex()
    {
        return FlopOffset.globalOffset(HOLE_CARDS, FLOP_CASE) +
               subIndex();
    }

    private int subIndex()
    {
        return FLOP_CASE.subIndex(
                HOLE[0].rank().ordinal(), HOLE[1].rank().ordinal(),
                FLOP[0].rank().ordinal(), FLOP[1].rank().ordinal(),
                FLOP[2].rank().ordinal());
    }


    //--------------------------------------------------------------------
    public Turn isoTurn(Card turnCard)
    {
        return new Turn(this, turnCard);
    }

    public Order refineOrder(Order with)
    {
        return ORDER.refine( with );
    }


    //--------------------------------------------------------------------
    public CanonCard[] refineHole(Order with)
    {
        return HOLE_CARDS.asWild(HOLE, with);
    }

    public CanonCard[] refineFlop(Order with)
    {
        if (!(FLOP[0].isWild() ||
              FLOP[1].isWild() ||
              FLOP[2].isWild())) return FLOP;

        CanonCard wildFlop[] = new CanonCard[]{
                with.asWild(FLOP_A),
                with.asWild(FLOP_B),
                with.asWild(FLOP_C)};
        Arrays.sort(wildFlop);
        return wildFlop;
    }


    //--------------------------------------------------------------------
    public Order order()
    {
        return ORDER;
    }

    public CanonCard[] hole()
    {
        return HOLE;
    }
    public CanonCard[] flop()
    {
        return FLOP;
    }


    //--------------------------------------------------------------------
    public boolean hasWildHole()
    {
        return HOLE[0].isWild() || HOLE[1].isWild();
    }

    public boolean hasWildFlop()
    {
        return FLOP[0].isWild() || FLOP[1].isWild() || FLOP[2].isWild();
    }


    //--------------------------------------------------------------------
    private static Order orderSuitsBy(
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
            return Order.triplet(sA, sB, sC);
        }
        else if (distinctRanks == 2)
        {
            if (distinctSuits == 2)
            {
                return partSuited(sA, sB, sC);
            }
            else
            {
                return flopA.rank() == flopB.rank()
                       ? Order.partSuited(sA, sB, sC)
                       : flopA.rank() == flopC.rank()
                         ? Order.partSuited(sA, sC, sB)
                         : Order.partSuited(sB, sC, sA);
            }
        }
        else
        {
            if (distinctSuits == 1)
            {
                return Order.suited(sA);
            }
            else if (distinctSuits == 2)
            {
                return partSuited(sA, sB, sC);
            }
            else
            {
                return Order.ordered(sA, sB, sC);
            }
        }
    }

    private static Order partSuited(Suit a, Suit b, Suit c)
    {
        return a == b
               ? Order.partSuited(a, c)
               : a == c
                 ? Order.partSuited(a, b)
                 : Order.partSuited(b, a);
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

        Flop isoFlop = (Flop) o;
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
