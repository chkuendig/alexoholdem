package ao.holdem.bot.opp_model.predict.act;


/**
 *
 */
public interface CumulativeStatistic<T extends CumulativeStatistic>
        extends Statistic//, CumulativeState
{
    public T prototype();
}
