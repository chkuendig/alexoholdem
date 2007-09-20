package ao.ai.opp_model.decision.graph;

/**
 * 
 */
public class JoinPoint<T> implements GraphTransform
{
    //--------------------------------------------------------------------
    private DecisionGraph<T> joined[];
    private double           length;
    private double           lengthSavings;


    //--------------------------------------------------------------------
    public JoinPoint(DecisionGraph<T> leafs[])
    {
        joined = leafs;

        double lengthBefore = joined[0].root().messageLength();
        apply();
        length = joined[0].root().messageLength();
        unapply();
        lengthSavings = lengthBefore - length;
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
    public boolean savesMoreThan(JoinPoint<T> point)
    {
        return lengthSavings > point.lengthSavings;
    }
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

    public boolean savesLength()
    {
        return lengthSavings > 0;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return String.valueOf(Math.round(length));
    }
}
