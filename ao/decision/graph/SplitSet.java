package ao.decision.graph;

import ao.decision.attr.AttributeSet;

import java.util.*;

/**
 *
 */
public class SplitSet<T>
{
    //--------------------------------------------------------------------
    private double        cutoff;
    private SplitPoint<T> shortest;
    private Map<DecisionGraph<T>, Collection<AttributeSet<?>>> savings;


    //--------------------------------------------------------------------
    public SplitSet(double cutoffLength)
    {
        cutoff   = cutoffLength;
        savings  = new HashMap<DecisionGraph<T>,
                              Collection<AttributeSet<?>>>();
        shortest = new SplitPoint<T>();
    }


    //--------------------------------------------------------------------
    public void add(DecisionGraph<T> leaf,
                    AttributeSet<?>  attr)
    {
        SplitPoint<T> split = new SplitPoint<T>(leaf, attr);
        if (split.shorterThan( cutoff ))
        {
            if (split.shorterThan(shortest))
            {
                shortest = split;
            }
            savingsFor(leaf).add( attr );
        }
    }

    private Collection<AttributeSet<?>> savingsFor(
            DecisionGraph<T> forGraph)
    {
        Collection<AttributeSet<?>> attributes = savings.get(forGraph);
        if (attributes == null)
        {
            attributes = new ArrayList<AttributeSet<?>>();
            savings.put(forGraph, attributes);
        }
        return attributes;
    }



    //--------------------------------------------------------------------
    public SplitPoint<T> shortest()
    {
        return shortest;
    }


    //--------------------------------------------------------------------
    public boolean isJoinUseful(
            DecisionGraph<T> leafs[])
    {
        Set<AttributeSet<?>> common = null;

        for (DecisionGraph<T> leaf : leafs)
        {
            if (! savings.containsKey(leaf)) return false;

            if (common == null)
            {
                common = new HashSet<AttributeSet<?>>(
                        savings.get(leaf));
            }
            else
            {
                common.retainAll( savings.get(leaf) );
                if (common.isEmpty()) return false;
            }
        }

        return true;
    }
}
