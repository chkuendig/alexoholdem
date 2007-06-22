package ao.holdem.def.model.cards;

import ao.holdem.def.model.card.Card;


/**
 *
 */
public class Hole
{
    //--------------------------------------------------------------------
    private final Card FIRST;
    private final Card SECOND;


    //--------------------------------------------------------------------
    public Hole(Card first, Card second)
    {
        FIRST  = (first != null ? first : second);
        SECOND = (first == null ? null  : second);
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


    //--------------------------------------------------------------------
    public boolean bothCardsVisible()
    {
        return FIRST != null && SECOND != null;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return "[" + FIRST + ", " + SECOND + "]";
    }
}
