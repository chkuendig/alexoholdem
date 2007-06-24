package ao.holdem.def.state.domain;

/**
 *
 */
public enum BetsToCall
{
    ZERO, ONE, TWO, THREE, FOUR;

    public static BetsToCall from(int toCall, int betSize)
    {
        int betsToCall = (toCall + 1) / betSize;
        return values()[ betsToCall ];
    }
}
