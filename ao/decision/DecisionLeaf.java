package ao.decision;

import ao.decision.attr.Attribute;
import ao.decision.data.Context;

/**
 *
 */
public class DecisionLeaf<T>
        extends DecisionTree
{
    //--------------------------------------------------------------------
    private Histogram<T> prediction;


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public DecisionLeaf(Histogram<T> hist)
    {
        super(null);
        prediction = hist;
    }

    @SuppressWarnings("unchecked")
    public static <T> DecisionTree<T>
            newInstance(Histogram<T> hist)
    {
        return new DecisionLeaf<T>( hist );
    }


    //--------------------------------------------------------------------
    public void addNode(Attribute    attribute,
                        DecisionTree tree)
    {
        throw new RuntimeException("cannot add Node to Leaf");
    }


    //--------------------------------------------------------------------
    public Histogram<T> predict(Context basedOn)
    {
        return prediction;
    }

    public Histogram<T> frequencies()
    {
        return prediction;
    }
}
