package ao.ai.monte_carlo;

import ao.holdem.engine.persist.Event;
import ao.holdem.model.Money;

import java.util.List;

/**
 *
 */
public class ProbableRollout extends Rollout
{
    //----------------------------------------------------------------
    private final double expectedReward;


    //----------------------------------------------------------------
    public ProbableRollout(
            List<Event> events,
            Money       mainStakes,
            Money       potSize,
            boolean     mainReachedShowdown,
            double      expectedReward)
    {
        super(events, mainStakes, potSize, mainReachedShowdown);

        this.expectedReward = expectedReward;
    }


    //----------------------------------------------------------------
    public double expectedReward()
    {
        return expectedReward;
    }
}
