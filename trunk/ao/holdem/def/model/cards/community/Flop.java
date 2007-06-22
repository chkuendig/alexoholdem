package ao.holdem.def.model.cards.community;

import ao.holdem.def.model.card.Card;

/**
 *
 */
public class Flop //extends Hole
{
    //--------------------------------------------------------------------
    private final Card FIRST;
    private final Card SECOND;
    private final Card THIRD;


    //--------------------------------------------------------------------
    public Flop(Card first, Card second, Card third)
    {
        assert first  != null &&
               second != null &&
               third  != null;

        FIRST  = first;
        SECOND = second;
        THIRD = third;
    }


    //--------------------------------------------------------------------
    public Card first()
    {
        return FIRST;
    }

    public Card second()
    {
        return SECOND;
    }
    
    public Card third()
    {
        return THIRD;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return FIRST + ", " + SECOND + ", " + THIRD;
    }
}
