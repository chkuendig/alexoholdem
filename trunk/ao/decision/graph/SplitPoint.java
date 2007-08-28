package ao.decision.graph;

import ao.decision.attr.AttributeSet;

/**
 *
 */
public class SplitPoint<T> implements GraphTransform
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
}
