package ao.stats.impl;

import ao.ai.opp_model.decision.attr.Attribute;
import ao.ai.opp_model.decision.attr.AttributePool;
import ao.stats.Statistic;

import java.util.Collection;
import java.util.ArrayList;

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
    public Collection<Attribute<?>> stats(AttributePool pool)
    {
        Collection<Attribute<?>> attributes =
                new ArrayList<Attribute<?>>();

        for (Statistic stat : delegets)
        {
            attributes.addAll( stat.stats(pool) );
        }

        return attributes;
    }
}
