package ao.stats.impl;

import ao.ai.opp_model.model.data.HoldemContext;
import ao.ai.opp_model.decision2.data.DataPool;
import ao.stats.Statistic;

/**
 *
 */
public class MultiStatistic implements Statistic
{
    //--------------------------------------------------------------------
    private Statistic delegets[];


    //--------------------------------------------------------------------
    public MultiStatistic(Statistic... stats)
    {
        delegets = stats;
    }


    //--------------------------------------------------------------------
    public HoldemContext nextActContext(DataPool pool)
    {
        HoldemContext ctx = null;

        for (Statistic stat : delegets)
        {
            HoldemContext statCtx = stat.nextActContext(pool);
            ctx = (ctx == null)
                    ? new HoldemContext( statCtx )
                    : ctx.merge(         statCtx );
        }

        return ctx;
    }
}
