package ao.ai.opp_model.decision.domain;

/**
 * 
 */
public enum BetRatio
{
    //--------------------------------------------------------------------
    SMALL, MEDIUM, LARGE;


    //--------------------------------------------------------------------
    public static BetRatio fromBetRatio(double ratio)
    {
        return (ratio <= 0.06)
                ? SMALL
                : ratio <= 0.24
                  ? MEDIUM
                  : LARGE;
    }
}
