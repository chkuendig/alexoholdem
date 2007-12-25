package ao.stats.impl;

import ao.ai.opp_model.decision.input.raw.example.Context;
import ao.ai.opp_model.decision.input.raw.example.ContextImpl;
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
    public Context nextActContext()
    {
        Context ctx = null;

        for (Statistic stat : delegets)
        {
            Context statCtx = stat.nextActContext();

            if (ctx == null)
            {
                ctx = new ContextImpl( statCtx );
            }
            else
            {
                ctx.addAll( statCtx );
            }
        }

        return ctx;
    }

//    public EnumSet<ContextDomain> nextActDomains()
//    {
//        EnumSet<ContextDomain> domains =
//                EnumSet.allOf( ContextDomain.class );
//
//        for (Statistic stat : delegets)
//        {
//            domains.retainAll( stat.nextActDomains() );
//        }
//
//        return domains;
//    }
}
