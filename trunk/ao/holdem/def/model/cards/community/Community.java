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
    private final Flop  FLOP;
    private final Turn  TURN;
    private final River RIVER;
    private final int   KNOWN_COUNT;


    //--------------------------------------------------------------------
    public Community()
    {
        this(null, null, null);
    }
    public Community(Flop flop)
    {
        this(flop, null, null);
    }

    public Community(Turn turn)
    {
        this(turn, turn, null);
    }

    public Community(River river)
    {
        this(river, river, river);
    }

    private Community(Flop flop, Turn turn, River river)
    {
        FLOP  = flop;
        TURN  = turn;
        RIVER = river;

        KNOWN_COUNT = countKnown();
    }


    //--------------------------------------------------------------------
    public int knownCount()
    {
        return KNOWN_COUNT;
    }
    private int countKnown()
    {
        return (RIVER != null)
                ? 5
                : (TURN != null)
                   ? 4
                   : (FLOP != null)
                      ? 3 : 0;
    }

    public int unknownCount()
    {
        return 5 - KNOWN_COUNT;
    }


    //--------------------------------------------------------------------
    public boolean contains(Card card)
    {
        switch (knownCount())
        {
            case 5: if (RIVER.fifth() == card) return true;
            case 4: if (TURN.fourth() == card) return true;
            case 3: if (FLOP.first()  == card ||
                        FLOP.second() == card ||
                        FLOP.third()  == card) return true;
        }
        return false;
    }

    public Card[] known()
    {
        Card known[] = new Card[5];

        switch (knownCount())
        {
            case 5: known[4] = RIVER.fifth();
            case 4: known[3] = TURN.fourth();
            case 3: known[2] = FLOP.third();
                    known[1] = FLOP.second();
                    known[0] = FLOP.first();
        }

        return Arrays.copyOf(known, knownCount());
    }


    //--------------------------------------------------------------------
    /**
     * @return null if not at flop yet
     */
    public Flop flop()
    {
        return FLOP;
    }


    //--------------------------------------------------------------------
    /**
     * @return null if not at turn yet
     */
    public Turn turn()
    {
        return TURN;
    }


    //--------------------------------------------------------------------
    /**
     * @return null if not at river yet
     */
    public River river()
    {
        return RIVER;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return RIVER != null
               ? RIVER.toString()
               : TURN != null
                 ? TURN.toString()
                 : FLOP != null
                   ? FLOP.toString()
                   : "none";
    }
}
