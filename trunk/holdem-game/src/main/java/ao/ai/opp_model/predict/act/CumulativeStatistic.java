package ao.ai.opp_model.predict.act;


/**
 *
 */
public interface CumulativeStatistic<T extends CumulativeStatistic>
        extends Statistic//, CumulativeState
{
    public T prototype();
}
