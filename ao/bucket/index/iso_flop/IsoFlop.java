package ao.bucket.index.iso_flop;

import ao.bucket.index.iso_cards.Ordering;
import ao.bucket.index.iso_cards.wild.card.RankedSuited;
import ao.bucket.index.iso_cards.wild.card.WildCard;
import ao.bucket.index.iso_cards.wild.suit.WildMarkedSuit;
import ao.bucket.index.iso_turn.IsoTurn;
import static ao.bucket.index.iso_util.IsoCaseUtils.distinct;
import static ao.bucket.index.iso_util.IsoCaseUtils.sortByRank;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Rank;
import ao.holdem.model.card.Suit;

import java.util.Arrays;

/**
 * Date: Jun 18, 2008
 * Time: 2:57:55 PM
 */
public class IsoFlop
{
    //--------------------------------------------------------------------
    private final RankedSuited<Rank, WildMarkedSuit>
                           HOLE_A, HOLE_B;
    private final WildCard FLOP_A, FLOP_B, FLOP_C;

    private final Ordering ORDER;
    private final FlopCase FLOP_CASE;


    //--------------------------------------------------------------------
    public IsoFlop(Ordering holeOrder,
                   Card     hole[],
                   Card...  flop)
    {
        Ordering byFlop  = orderSuitsBy(flop);
        Ordering refined = holeOrder.refine( byFlop );
        ORDER = refined;

        WildCard wildHole[] = new WildCard[]{
                WildCard.newInstance(refined, hole[0]),
                WildCard.newInstance(refined, hole[1])};
        Arrays.sort(wildHole);
        HOLE_A = wildHole[ 0 ].mark(0);
        HOLE_B = wildHole[ 1 ].mark(
                    countLeftRankMatches(hole, hole[1], 1));

        WildCard wildFlop[] = new WildCard[]{
                WildCard.newInstance(refined, flop[ 0 ]),
                WildCard.newInstance(refined, flop[ 1 ]),
                WildCard.newInstance(refined, flop[ 2 ])};
        Arrays.sort(wildFlop);
        FLOP_A = wildFlop[ 0 ];
        FLOP_B = wildFlop[ 1 ];
        FLOP_C = wildFlop[ 2 ];

        FLOP_CASE = computeFlopCase();
    }


    //--------------------------------------------------------------------
    private int countLeftRankMatches(
            Card in[], Card of, int upTo)
    {
        int count = 0;
        for (int i = 0; i < upTo; i++)
        {
            if (in[i].rank() == of.rank())
            {
                count++;
            }
        }
        return count;
    }


    //--------------------------------------------------------------------
    public Rank holeA()
    {
        return HOLE_A.rank();
    }
    public Rank holeB()
    {
        return HOLE_B.rank();
    }

    public Rank flopA()
    {
        return FLOP_A.rank();
    }
    public Rank flopB()
    {
        return FLOP_B.rank();
    }
    public Rank flopC()
    {
        return FLOP_C.rank();
    }


    //--------------------------------------------------------------------
    public FlopCase flopCase()
    {
        return FLOP_CASE;
    }

    private FlopCase computeFlopCase()
    {
        return FlopCase.newInstance(
                HOLE_A.suit(), HOLE_B.suit(),
                FLOP_A.suit(), FLOP_B.suit(), FLOP_C.suit());
    }

    public int subIndex()
    {
        return FLOP_CASE.subIndex(
                holeA().ordinal(), holeB().ordinal(),
                flopA().ordinal(), flopB().ordinal(), flopC().ordinal());
    }


    //--------------------------------------------------------------------
    public IsoTurn isoTurn(Card hole[],
                           Card flop[],
                           Card turnCard)
    {
        return new IsoTurn(ORDER, hole, flop, turnCard);
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
        return //CASE +  " -> " +
                Arrays.toString(new RankedSuited[]{HOLE_A, HOLE_B}) +
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
        return a == b
               ? Ordering.partSuited(a, c)
               : a == c
                 ? Ordering.partSuited(a, b)
                 : Ordering.partSuited(b, a);
    }
}
