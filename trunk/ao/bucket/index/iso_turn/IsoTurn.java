package ao.bucket.index.iso_turn;

import ao.bucket.index.iso_cards.Ordering;
import ao.bucket.index.iso_cards.wild.card.RankedSuited;
import ao.bucket.index.iso_cards.wild.card.WildCard;
import ao.bucket.index.iso_cards.wild.suit.WildSuitMarker;
import ao.bucket.index.iso_river.IsoRiver;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Rank;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Date: Aug 27, 2008
 * Time: 10:53:23 AM
 *
 * suit isomorphic hand at turn
 */
public class IsoTurn
{
    //--------------------------------------------------------------------
    private final WildCard HOLE_A, HOLE_B;
    private final WildCard FLOP_A, FLOP_B, FLOP_C,
                           TURN;
    private final TurnCase CASE;
    private final Ordering ORDER;


    //--------------------------------------------------------------------
    public IsoTurn(Ordering flopOrder,
                   Card hole[],
                   Card     flop[],
                   Card     turn)
    {
        Ordering byTurn  = Ordering.suited(turn.suit());
        Ordering refined = flopOrder.refine( byTurn );
        ORDER            = refined;

        WildCard wildHole[] = new WildCard[]{
                WildCard.newInstance(refined, hole[0]),
                WildCard.newInstance(refined, hole[1])};
        sort(wildHole);
        HOLE_A = wildHole[ 0 ];
        HOLE_B = wildHole[ 1 ];

        WildCard wildFlop[] = new WildCard[]{
                WildCard.newInstance(refined, flop[ 0 ]),
                WildCard.newInstance(refined, flop[ 1 ]),
                WildCard.newInstance(refined, flop[ 2 ])};
        sort(wildFlop);
        FLOP_A = wildFlop[ 0 ];
        FLOP_B = wildFlop[ 1 ];
        FLOP_C = wildFlop[ 2 ];

        TURN = WildCard.newInstance(refined, turn);
        CASE = TurnCase.valueOf(
                HOLE_A, HOLE_B,
                FLOP_A, FLOP_B, FLOP_C,
                TURN);
    }

    private <S extends WildSuitMarker<S>>
            void sort(RankedSuited<Rank, S> wildCards[])
    {
        Arrays.sort(wildCards, new Comparator<RankedSuited<Rank, S>>()  {
            public int compare(RankedSuited<Rank, S> a,
                               RankedSuited<Rank, S> b) {
                int suitCmp = a.suit().compareTo( b.suit() );
                return (suitCmp == 0
                           ? a.rank().compareTo( b.rank() )
                           : suitCmp);
            }
        });
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

    public Rank turn()
    {
        return TURN.rank();
    }


    //--------------------------------------------------------------------
    public IsoRiver isoRiver(Card hole[],
                             Card flop[],
                             Card turn,
                             Card river)
    {
        return new IsoRiver(ORDER,
                            hole, flop, turn, river);
    }


    //--------------------------------------------------------------------
    public int caseIndex()
    {
        return subCase().indexOf( TURN.suit() );
    }

    public TurnCase subCase()
    {
        return CASE;
    }


    //--------------------------------------------------------------------
    public int casedSubIndex(TurnCaseSet caseSet)
    {
        TurnCase subCase =
                caseSet.isFalsed()
                ? caseSet.trueCase()
                : subCase();

        int localOffset = subCase.localOffset( TURN.suit() );
        return subIndex() + localOffset;
    }
    public int subIndex()
    {
        int index         = 0;
        int suitMatches[] = new int[5];
        if (TURN.suit() == HOLE_A.suit())
            suitMatches[ index++ ] = HOLE_A.rank().ordinal();
        if (TURN.suit() == HOLE_B.suit())
            suitMatches[ index++ ] = HOLE_B.rank().ordinal();
        if (TURN.suit() == FLOP_A.suit())
            suitMatches[ index++ ] = FLOP_A.rank().ordinal();
        if (TURN.suit() == FLOP_B.suit())
            suitMatches[ index++ ] = FLOP_B.rank().ordinal();
        if (TURN.suit() == FLOP_C.suit())
            suitMatches[ index++ ] = FLOP_C.rank().ordinal();

        int offset = 0;
        for (int i = 0; i < index; i++)
        {
            if (suitMatches[i] < TURN.rank().ordinal())
            {
                offset--;
            }
        }
        return TURN.rank().ordinal() + offset;
    }

//    public int subIndexCount()
//    {
//        int suitMatches = 0;
//        if (TURN.suit() == HOLE_A.suit()) suitMatches++;
//        if (TURN.suit() == HOLE_B.suit()) suitMatches++;
//        if (TURN.suit() == FLOP_A.suit()) suitMatches++;
//        if (TURN.suit() == FLOP_B.suit()) suitMatches++;
//        if (TURN.suit() == FLOP_C.suit()) suitMatches++;
//        return 13 - suitMatches;
//    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return Arrays.toString(new RankedSuited[]{HOLE_A, HOLE_B}) +
               Arrays.toString(new WildCard[]{FLOP_A, FLOP_B, FLOP_C}) +
               "[" + TURN + "]";
    }

    
    //--------------------------------------------------------------------
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IsoTurn isoTurn = (IsoTurn) o;
        return
               HOLE_A.equals(isoTurn.HOLE_A) &&
               HOLE_B.equals(isoTurn.HOLE_B) &&

               FLOP_A.equals(isoTurn.FLOP_A) &&
               FLOP_B.equals(isoTurn.FLOP_B) &&
               FLOP_C.equals(isoTurn.FLOP_C) &&

               TURN.equals(isoTurn.TURN);
    }

    public int hashCode()
    {
        int result = 0;

        result = 31 * result + HOLE_A.hashCode();
        result = 31 * result + HOLE_A.hashCode();

        result = 31 * result + FLOP_A.hashCode();
        result = 31 * result + FLOP_B.hashCode();
        result = 31 * result + FLOP_C.hashCode();

        result = 31 * result + TURN.hashCode();

        return result;
    }
}
