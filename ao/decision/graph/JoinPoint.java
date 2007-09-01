package ao.decision.graph;

/**
 * 
 */
public class JoinPoint<T> implements GraphTransform
{
    //--------------------------------------------------------------------
    private DecisionGraph<T> joined[];
    private double           length;


    //--------------------------------------------------------------------
    public JoinPoint(DecisionGraph<T> leafs[])
    {
        joined = leafs;

        apply();
        joined[0].root().toString();
        length = joined[0].root().messageLength();
        unapply();
    }


    //--------------------------------------------------------------------
    public void apply()
    {
        DecisionGraph.join( joined );
    }
    public void unapply()
    {
        DecisionGraph.unjoin( joined );
    }


    //--------------------------------------------------------------------
    public boolean isUseful(SplitSet<T> accordingTo)
    {
        return accordingTo.isJoinUseful( joined );
    }


    //--------------------------------------------------------------------
    public boolean shorterThan(JoinPoint<T> point)
    {
        return shorterThan(point.length);
    }
    public boolean shorterThan(double cutoffLength)
    {
        return length < cutoffLength;
    }
    public boolean shorterThan(SplitPoint<T> split)
    {
        return !split.shorterThan( length );
    }
}
