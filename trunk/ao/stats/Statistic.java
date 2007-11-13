package ao.stats;

import ao.ai.opp_model.model.data.HoldemContext;
import ao.ai.opp_model.decision.data.DataPool;

/**
 *
 */
public interface Statistic
{
    public HoldemContext nextActContext(DataPool pool);
}
