package ao.bucket.index.turn;

import ao.bucket.index.card.CanonCard;
import ao.bucket.index.card.CanonSuit;
import ao.bucket.index.card.Order;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.river.CanonRiver;
import ao.holdem.model.card.Card;

/**
 * Date: Aug 27, 2008
 * Time: 10:53:23 AM
 *
 * suit isomorphic hand at turn
 */
public class Turn
{
    //--------------------------------------------------------------------
    private final CanonCard HOLE[], FLOP[], TURN;
    private final Flop      FLOP_CARDS;
    private final Card      TURN_CARD;
    private final Order     ORDER;
    private final int       CANON_INDEX;


    //--------------------------------------------------------------------
    public Turn(Flop flop,
                Card turn)
    {
        ORDER       = flop.refineOrder(
                         Order.suited(turn.suit()));
        HOLE        = flop.refineHole(ORDER);
        FLOP        = flop.refineFlop(ORDER);
        TURN        = ORDER.asCanon(turn);
        FLOP_CARDS  = flop;
        TURN_CARD   = turn;
        CANON_INDEX = computeCanonIndex(flop.canonIndex());
    }


    //--------------------------------------------------------------------
    public CanonRiver addRiver(Card river)
    {
        return new CanonRiver(this, river);
    }

    public Order refineOrder(Order with)
    {
        return ORDER.refine( with );
    }

    public CanonCard[] refineHole(Order with)
    {
        return FLOP_CARDS.refineHole(HOLE, with);
    }

    public CanonCard[] refineFlop(Order with)
    {
        return FLOP_CARDS.refineFlop(FLOP, with);
    }

    public CanonCard refineTurn(Order with)
    {
        return TURN.isWild()
               ? with.asCanon(TURN_CARD)
               : TURN;
    }


    //--------------------------------------------------------------------
    public int canonIndex()
    {
        return CANON_INDEX;
    }
    public int computeCanonIndex(int flopIndex)
    {
        return TurnLookup.canonIndex(flopIndex, TURN);
    }

    public CanonSuit turnSuit()
    {
        return TURN.suit();
    }



//    //--------------------------------------------------------------------
//    public int casedSubIndex(TurnCaseSet caseSet)
//    {
//        TurnCase subCase =
//                caseSet.isFalsed()
//                ? caseSet.trueCase()
//                : subCase();
//
//        int localOffset = subCase.localOffset( TURN.suit() );
//        return subIndex() + localOffset;
//    }
//    public int subIndex()
//    {
//        int index         = 0;
//        int suitMatches[] = new int[5];
//        if (TURN.suit() == HOLE_A.suit())
//            suitMatches[ index++ ] = HOLE_A.rank().ordinal();
//        if (TURN.suit() == HOLE_B.suit())
//            suitMatches[ index++ ] = HOLE_B.rank().ordinal();
//        if (TURN.suit() == FLOP_A.suit())
//            suitMatches[ index++ ] = FLOP_A.rank().ordinal();
//        if (TURN.suit() == FLOP_B.suit())
//            suitMatches[ index++ ] = FLOP_B.rank().ordinal();
//        if (TURN.suit() == FLOP_C.suit())
//            suitMatches[ index++ ] = FLOP_C.rank().ordinal();
//
//        int offset = 0;
//        for (int i = 0; i < index; i++)
//        {
//            if (suitMatches[i] < TURN.rank().ordinal())
//            {
//                offset--;
//            }
//        }
//        return TURN.rank().ordinal() + offset;
//    }
//
////    public int subIndexCount()
////    {
////        int suitMatches = 0;
////        if (TURN.suit() == HOLE_A.suit()) suitMatches++;
////        if (TURN.suit() == HOLE_B.suit()) suitMatches++;
////        if (TURN.suit() == FLOP_A.suit()) suitMatches++;
////        if (TURN.suit() == FLOP_B.suit()) suitMatches++;
////        if (TURN.suit() == FLOP_C.suit()) suitMatches++;
////        return 13 - suitMatches;
////    }
//
//
    //--------------------------------------------------------------------
    public String toString()
    {
//        return Arrays.toString(HOLE) +
//               Arrays.toString(FLOP) +
//               "[" + TURN + "]";
        return FLOP_CARDS + "\t" + TURN_CARD;
    }

//
//    //--------------------------------------------------------------------
//    public boolean equals(Object o)
//    {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        IsoTurn isoTurn = (IsoTurn) o;
//        return
//               HOLE_A.equals(isoTurn.HOLE_A) &&
//               HOLE_B.equals(isoTurn.HOLE_B) &&
//
//               FLOP_A.equals(isoTurn.FLOP_A) &&
//               FLOP_B.equals(isoTurn.FLOP_B) &&
//               FLOP_C.equals(isoTurn.FLOP_C) &&
//
//               TURN.equals(isoTurn.TURN);
//    }
//
//    public int hashCode()
//    {
//        int result = 0;
//
//        result = 31 * result + HOLE_A.hashCode();
//        result = 31 * result + HOLE_A.hashCode();
//
//        result = 31 * result + FLOP_A.hashCode();
//        result = 31 * result + FLOP_B.hashCode();
//        result = 31 * result + FLOP_C.hashCode();
//
//        result = 31 * result + TURN.hashCode();
//
//        return result;
//    }
}
