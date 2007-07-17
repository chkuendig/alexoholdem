package ao.holdem.def.model.cards.community;

import ao.holdem.def.model.card.Card;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Immutable.
 */
public class Community implements Serializable
{
    //--------------------------------------------------------------------
    private Card FLOP_A;
    private Card FLOP_B;
    private Card FLOP_C;
    private Card TURN;
    private Card RIVER;


    //--------------------------------------------------------------------
    public Community()
    {
        this(null, null, null, null, null);
    }
    public Community(Card flopA, Card flopB, Card flopC)
    {
        this(flopA, flopB, flopC, null, null);
    }

    public Community(Turn turn)
    {
        this(turn, turn, null);
    }

    public Community(River river)
    {
        this(river, river, river);
    }

    private Community(
            Card flopA,
            Card flopB,
            Card flopC,
            Card turn,
            Card river)
    {
        FLOP_A = flopA;
        FLOP_B = flopB;
        FLOP_C = flopC;
        TURN = turn;
        RIVER = river;
    }


    //--------------------------------------------------------------------
    public int knownCount()
    {
        return hasRiver()
                ? 5
                : hasTurn()
                   ? 4
                   : hasFlop()
                      ? 3 : 0;
    }

    public int unknownCount()
    {
        return 5 - knownCount();
    }


    //--------------------------------------------------------------------
    public boolean contains(Card card)
    {
        return FLOP_A == card ||
               FLOP_B == card ||
               FLOP_C == card ||
               TURN == card ||
               RIVER == card;
    }


    //--------------------------------------------------------------------
    public Card[] known()
    {
        Card known[] = new Card[5];

        switch (knownCount())
        {
            case 5: known[4] = RIVER;
            case 4: known[3] = TURN;
            case 3: known[2] = FLOP_C;
                    known[1] = FLOP_B;
                    known[0] = FLOP_A;
        }

        return Arrays.copyOf(known, knownCount());
    }


    //--------------------------------------------------------------------
    public boolean hasFlop()
    {
        return FLOP_A != null;
    }

    public boolean hasTurn()
    {
        return TURN != null;
    }

    public boolean hasRiver()
    {
        return RIVER != null;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return Arrays.toString( known() );
    }
}
