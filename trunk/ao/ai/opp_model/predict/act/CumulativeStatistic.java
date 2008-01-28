package ao.ai.opp_model.predict.act;

import ao.holdem.engine.state.CumulativeState;

/**
 *
 */
public interface CumulativeStatistic<T extends CumulativeStatistic>
        extends Statistic, CumulativeState
{
    public T prototype();
}
