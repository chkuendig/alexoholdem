package ao.decision.graph;

/**
 *
 */
public class JoinSet<T>
{
    //--------------------------------------------------------------------
    private SplitPoint<T> cutoff;
    private SplitSet<T>   splits;
    private JoinPoint<T>  shortest;


    //--------------------------------------------------------------------
    public JoinSet(SplitSet<T> splitSet)
    {
        cutoff = splitSet.shortest();
        splits = splitSet;
    }


    //--------------------------------------------------------------------
    public void add(DecisionGraph<T> leafs[])
    {
        JoinPoint<T> point = new JoinPoint<T>(leafs);
        if (! point.shorterThan( cutoff )) return;
        if (! point.isUseful(    splits )) return;

        if (shortest == null || point.shorterThan(shortest))
        {
            shortest = point;
        }
    }

    //--------------------------------------------------------------------
    public JoinPoint<T> shortestUseful()
    {
        return shortest;
    }
}
