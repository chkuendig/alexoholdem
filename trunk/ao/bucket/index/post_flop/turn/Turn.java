package ao.bucket.index.post_flop.turn;

import ao.bucket.index.card.CanonCard;
import ao.bucket.index.card.Order;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.post_flop.common.PostFlopCase;
import ao.holdem.model.card.Card;

import java.util.Arrays;

/**
 * Date: Aug 27, 2008
 * Time: 10:53:23 AM
 *
 * suit isomorphic hand at turn
 */
public class Turn
{
    //--------------------------------------------------------------------
    private final CanonCard    HOLE[], FLOP[], TURN;
    private final Flop         FLOP_CARDS;
    private final PostFlopCase CASE;
    private final Order        ORDER;


    //--------------------------------------------------------------------
    public Turn(Flop flop,
                Card turn)
    {
        ORDER      = flop.refineOrder(
                        Order.suited(turn.suit()));
        HOLE       = flop.refineHole(ORDER);
        FLOP       = flop.refineFlop(ORDER);
        TURN       = ORDER.asWild(turn);
        CASE       = PostFlopCase.valueOf(HOLE, FLOP, TURN);
        FLOP_CARDS = flop;
    }

//    //--------------------------------------------------------------------
//    public IsoRiver isoRiver(Card hole[],
//                             Card flop[],
//                             Card turn,
//                             Card river)
//    {
//        return new IsoRiver(ORDER,
//                            hole, flop, turn, river);
//    }


    //--------------------------------------------------------------------
    public int canonIndex(int flopIndex)
    {
        return TurnLookup.globalOffset(flopIndex) +
               TurnLookup.caseSet(flopIndex).offset(CASE) +
               CASE.subIndex(HOLE, FLOP, TURN);
//        int index =
//                TurnLookup.caseSet(flopIndex).offset(CASE) +
//                CASE.subIndex(HOLE, FLOP, TURN);
//        System.out.println(this.toString() + "\t" +
//                           TurnLookup.caseSet(flopIndex) + "\t" +
//                           index);
//        return index;
    }

    public PostFlopCase turnCase()
    {
        return CASE;
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
        return Arrays.toString(HOLE) +
               Arrays.toString(FLOP) +
               "[" + TURN + "]";
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
