package ao.holdem.bots.opp_model.neat;

/**
 *
 */
public class InnovationCounter
{
    //--------------------------------------------------------------------
    private volatile int nextInnovation;


    //--------------------------------------------------------------------
    public InnovationNumber nextInnovation()
    {
        return new InnovationNumber(nextInnovation++);
    }
}
