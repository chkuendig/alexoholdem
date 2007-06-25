package ao.holdem.def.state.domain;

/**
 * Distance from dealer.
 */
public enum DealerDistance
{
    //--------------------------------------------------------------------
    ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE;


    //--------------------------------------------------------------------
    public static DealerDistance from(int awayFromDealer)
    {
        return values()[ awayFromDealer ];
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return ordinal() + " from dealer";
    }
}
