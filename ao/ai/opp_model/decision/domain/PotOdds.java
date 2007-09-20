package ao.ai.opp_model.decision.domain;

/**
 * 
 */
public enum PotOdds
{
    //--------------------------------------------------------------------
    SMALL, MEDIUM, LARGE;


    //--------------------------------------------------------------------
    public static PotOdds fromPotOdds(double odds)
    {
        return (odds <= 0.05)
                ? SMALL
                : odds <= 0.15
                  ? MEDIUM
                  : LARGE;
    }
}
