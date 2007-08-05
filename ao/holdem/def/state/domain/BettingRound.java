package ao.holdem.def.state.domain;

/**
 * 
 */
public enum BettingRound
{
    PREFLOP,
    FLOP,
    TURN,
    RIVER;

    public BettingRound previous()
    {
        return values()[ ordinal() - 1 ];
    }
}
