package ao.stats;

import ao.state.CumulativeState;

/**
 *
 */
public interface CumulativeStatistic<T extends CumulativeStatistic>
        extends Statistic, CumulativeState
{
    public T prototype();
}
