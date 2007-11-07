package ao.stats;

import ao.ai.opp_model.decision.data.HoldemContext;
import ao.ai.opp_model.decision2.data.DataPool;

/**
 *
 */
public interface Statistic
{
    public HoldemContext nextActContext(DataPool pool);
}
