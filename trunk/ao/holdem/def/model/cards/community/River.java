package ao.holdem.def.model.cards.community;

import ao.holdem.def.model.card.Card;

/**
 *
 */
public class River extends Turn
{
    //--------------------------------------------------------------------
    private final Card FIFTH;


    //--------------------------------------------------------------------
    public River(Card first, Card second, Card third,
                 Card forth, Card fifth)
    {
        super(first, second, third, forth);
        assert fifth != null;

        FIFTH = fifth;
    }

    public River(Turn fromTurn, Card fifth)
    {
        this(fromTurn.first(), fromTurn.second(), fromTurn.third(),
             fromTurn.fourth(), fifth);
    }


    //--------------------------------------------------------------------
    public Card fifth()
    {
        return FIFTH;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return super.toString() + ", " + FIFTH;
    }
}
