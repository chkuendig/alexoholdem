package ao.stats.impl;

import ao.ai.opp_model.decision.attr.AttributePool;
import ao.ai.opp_model.decision.context.ContextBuilder;
import ao.ai.opp_model.decision.context.HoldemContext;
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
    public HoldemContext nextActContext(AttributePool pool)
    {
        ContextBuilder ctx = null;

        for (Statistic stat : delegets)
        {
            HoldemContext statCtx = stat.nextActContext(pool);
            ctx = (ctx == null)
                    ? new ContextBuilder( statCtx )
                    : ctx.merge(          statCtx );
        }

        return ctx;
    }
}
