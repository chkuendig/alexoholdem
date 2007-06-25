package ao.holdem.def.state.domain;

/**
 *  number of opponents.
 */
public enum Opposition
{
    //--------------------------------------------------------------------
    ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE;


    //--------------------------------------------------------------------
    public static Opposition fromPlayers(int players)
    {
        return values()[ players - 2 ];
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return (ordinal() + 1) + " oppnts";
    }
}
