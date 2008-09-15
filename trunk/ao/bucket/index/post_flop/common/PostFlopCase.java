package ao.bucket.index.post_flop.common;

import ao.bucket.index.card.CanonCard;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;

/**
 * Date: Sep 1, 2008
 * Time: 8:38:55 PM
 */
public enum PostFlopCase
{
    //--------------------------------------------------------------------
    ZERO(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6);

    public static PostFlopCase VALUES[] = values();


    //--------------------------------------------------------------------
    public static PostFlopCase valueOf(
            CanonCard hole[],
            CanonCard flop[],
            CanonCard turn)
    {
        int count = 0;
        if (turn.suit() == hole[0].suit()) count++;
        if (turn.suit() == hole[1].suit()) count++;
        if (turn.suit() == flop[0].suit()) count++;
        if (turn.suit() == flop[1].suit()) count++;
        if (turn.suit() == flop[2].suit()) count++;

        return PostFlopCase.values()[ count ];
    }

    public static PostFlopCase valueOf(
            Hole hole,
            Card flopA, Card flopB, Card flopC,
            Card turn,
            Card river)
    {
        return null;
    }


    //--------------------------------------------------------------------
    private final int PRERIVER_SUITED;


    //--------------------------------------------------------------------
    private PostFlopCase(int preriverSuited)
    {
        PRERIVER_SUITED = preriverSuited;
    }


    //--------------------------------------------------------------------
    public int size()
    {
        return 13 - PRERIVER_SUITED;
    }


    //--------------------------------------------------------------------
    public int subIndex(CanonCard hole[],
                        CanonCard flop[],
                        CanonCard turn)
    {
        return turn.rank().ordinal() -
                offset(hole, flop, turn);
    }

    public int subIndex(CanonCard hole[],
                        CanonCard flop[],
                        CanonCard turn,
                        CanonCard river)
    {
        return river.rank().ordinal() -
                offset(hole, flop, river) -
                offset(turn, river);
    }


    //--------------------------------------------------------------------
    private int offset(CanonCard hole[],
                       CanonCard flop[],
                       CanonCard of)
    {
        return offset(hole[0], of) +
               offset(hole[1], of) +
               offset(flop[0], of) +
               offset(flop[1], of) +
               offset(flop[2], of);
    }
    private int offset(CanonCard precedent, CanonCard of)
    {
        return (precedent.suit() == of.suit()) &&
                precedent.rank().ordinal() <  of.rank().ordinal()
               ? 1 : 0;
    }
}
