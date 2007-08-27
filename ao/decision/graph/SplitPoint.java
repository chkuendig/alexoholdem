package ao.decision.graph;

import ao.decision.attr.AttributeSet;

/**
 *
 */
public class SplitPoint<T>
        //implements Comparable<SplitPoint<T>>
{
    //--------------------------------------------------------------------
    private double           length;
    private DecisionGraph<T> leaf;
    private AttributeSet<?>  attr;


    //--------------------------------------------------------------------
    public SplitPoint()
    {
        length = Double.MAX_VALUE;
    }

    public SplitPoint(DecisionGraph<T> leaf,
                      AttributeSet<?>  attr)
    {
        this.leaf = leaf;
        this.attr = attr;

        apply();
        length = leaf.root().messageLength();
        unapply();
    }


    //--------------------------------------------------------------------
//    public boolean leafEquals(DecisionGraph<T> graph)
//    {
//        return leaf.equals( graph );
//    }


    //--------------------------------------------------------------------
    public DecisionGraph<T> leaf()
    {
        return leaf;
    }
    public AttributeSet<?> attributeType()
    {
        return attr;
    }


    //--------------------------------------------------------------------
    public void apply()
    {
        leaf.split(attr);
    }
    public void unapply()
    {
        leaf.unsplit();
    }


    //--------------------------------------------------------------------
    public boolean shorterThan(SplitPoint<T> otherSplit)
    {
        return shorterThan(otherSplit.length);
    }
    public boolean shorterThan(double cutoff)
    {
        return length < cutoff;
    }
//    public int compareTo(SplitPoint<T> o)
//    {
//        return Double.compare(length, o.length);
//    }
}
