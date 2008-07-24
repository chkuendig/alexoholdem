package ao.bucket.index.iso_cards;

import ao.bucket.index.iso_case.CommunityCase;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Suit;

import java.util.Arrays;

/**
 * Date: Jun 18, 2008
 * Time: 2:57:55 PM
 */
public class IsoFlop
{
    //--------------------------------------------------------------------
    //private final Card          CARDS[];
//    private final Ordering      HOLE_ORDER;
    private final CommunityCase CASE;
    private final WildCard      HOLE_A, HOLE_B,
                                FLOP_A, FLOP_B, FLOP_C;


    //--------------------------------------------------------------------
    public IsoFlop(Ordering holeOrder,
                   Card     hole[],
                   Card...  flop)
    {
        //CARDS = Arrays.copyOf(hole, hole.length + flop.length);
        //System.arraycopy(flop, 0, CARDS, hole.length, flop.length);

//        HOLE_ORDER = holeOrder;

        CASE  = new CommunityCase(flop, hole);

        Ordering byFlop  = orderSuitsBy(flop);
        Ordering refined = holeOrder.refine( byFlop );

        Card holeInOrder[] = hole.clone();
        Arrays.sort(holeInOrder, Card.BY_RANK_DSC);
        HOLE_A = asWild(refined, holeInOrder[0]);
        HOLE_B = asWild(refined, holeInOrder[1]);

        Card flopInOrder[] = flop.clone();
        Arrays.sort(flopInOrder, Card.BY_RANK_DSC);
        FLOP_A = asWild(refined, flopInOrder[0]);
        FLOP_B = asWild(refined, flopInOrder[1]);
        FLOP_C = asWild(refined, flopInOrder[2]);
    }

    private WildCard asWild(Ordering order, Card card)
    {
        return new WildCard(card.rank(),
                            order.asWild(card.suit()));
    }


    //--------------------------------------------------------------------
    private Ordering orderSuitsBy(Card... flop)
    {
        Card byRank[] = sortByRank(flop);
        Card a = byRank[0],
             b = byRank[1],
             c = byRank[2];
        Suit sA = a.suit(),
             sB = b.suit(),
             sC = c.suit();

        int distinctRanks = distinct(a.rank(), b.rank(), c.rank());
        int distinctSuits = distinct(sA      , sB      , sC      );

        if (distinctRanks == 1)
        {
            assert distinctSuits == 3;
            return Ordering.triplet(sA, sB, sC);
        }
        else if (distinctRanks == 2)
        {
            if (distinctSuits == 2)
            {
                return suitedPlus(sA, sB, sC);
            }
            else
            {
                assert distinctSuits == 3;
                return a.rank() == b.rank()
                       ? Ordering.partSuited(sA, sB, sC)
                       : a.rank() == c.rank()
                         ? Ordering.partSuited(sA, sC, sB)
                         : Ordering.partSuited(sB, sC, sA);
            }
        }
        else
        {
            assert distinctRanks == 3;
            if (distinctSuits == 1)
            {
                return Ordering.suited(sA);
            }
            else if (distinctSuits == 2)
            {
                return suitedPlus(sA, sB, sC);
            }
            else
            {
                assert distinctSuits == 3;
                return Ordering.ordered(sA, sB, sC);
            }
        }
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        //return CASE + " -> " + ORDER;
//        return CASE + " :: " + ORDER +  " -> " +
//                Arrays.toString(new WildCard[]{FLOP_A, FLOP_B, FLOP_C});
//        return CASE + " :: " + HOLE_ORDER +  " -> " +
//                Arrays.toString(new WildCard[]{FLOP_A, FLOP_B, FLOP_C});
//        return CASE +  " -> " +
//                Arrays.toString(new WildCard[]{FLOP_A, FLOP_B, FLOP_C});
        return CASE +  " -> " +
                Arrays.toString(new WildCard[]{HOLE_A, HOLE_B}) +
                Arrays.toString(new WildCard[]{FLOP_A, FLOP_B, FLOP_C});
    }


    //--------------------------------------------------------------------
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IsoFlop isoFlop = (IsoFlop) o;
        return
//               CASE .equals( isoFlop.CASE  ) &&
//               HOLE_ORDER.equals( isoFlop.HOLE_ORDER ) &&

               HOLE_A.equals(isoFlop.HOLE_A) &&
               HOLE_B.equals(isoFlop.HOLE_B) &&

               FLOP_A.equals(isoFlop.FLOP_A) &&
               FLOP_B.equals(isoFlop.FLOP_B) &&
               FLOP_C.equals(isoFlop.FLOP_C);
    }

    @Override
    public int hashCode()
    {
        int result = 0;
//        result = CASE.hashCode();
//        result = 31 * result + HOLE_ORDER.hashCode();

        result = 31 * result + HOLE_A.hashCode();
        result = 31 * result + HOLE_A.hashCode();

        result = 31 * result + FLOP_A.hashCode();
        result = 31 * result + FLOP_B.hashCode();
        result = 31 * result + FLOP_C.hashCode();

        return result;
    }


    //--------------------------------------------------------------------
    private static Ordering suitedPlus(Suit a, Suit b, Suit c)
    {
//        return a == b
//               ? Ordering.partSuited(a, b, c)
//               : a == c
//                 ? Ordering.partSuited(a, c, b)
//                 : Ordering.partSuited(b, c, a);
        return a == b
               ? Ordering.partSuited(a, c)
               : a == c
                 ? Ordering.partSuited(a, b)
                 : Ordering.partSuited(b, a);
    }

    private static Card[] sortByRank(Card... cards)
    {
        Card copy[] = cards.clone();
        Arrays.sort(copy, Card.BY_RANK_DSC);
        return copy;
    }

    private static <T> int distinct(T a, T b, T c)
    {
        return a == b && b == c
               ? 1
               : a != b && a != c && b != c
                 ? 3 : 2;
    }
}
