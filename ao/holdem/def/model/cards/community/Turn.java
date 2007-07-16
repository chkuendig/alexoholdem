package ao.holdem.def.model.cards.community;

import ao.holdem.def.model.card.Card;

import java.io.Serializable;


/**
 *
 */
public class Turn
        extends Flop
        implements Serializable
{
    //--------------------------------------------------------------------
    private final Card FOURTH;


    //--------------------------------------------------------------------
    public Turn(Card first, Card second, Card third, Card fourth)
    {
        super(first, second, third);
        assert fourth != null;

        FOURTH = fourth;
    }

    public Turn(Flop fromFlop, Card fourth)
    {
        this(fromFlop.first(), fromFlop.second(), fromFlop.third(),
             fourth);
    }


    //--------------------------------------------------------------------
    public Card fourth()
    {
        return FOURTH;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return super.toString() + ", " + FOURTH;
    }
}
