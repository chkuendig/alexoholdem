package ao.holdem.model;

/**
 * 
 */
public enum BettingRound
{
    PREFLOP,
    FLOP,
    TURN,
    RIVER;

//    public BettingRound previous()
//    {
//        return values()[ ordinal() - 1 ];
//    }

    public BettingRound next()
    {
        return (this == RIVER)
                ? null
                : values()[ ordinal() + 1 ];
    }
}
