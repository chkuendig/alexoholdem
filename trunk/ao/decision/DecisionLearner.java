package ao.decision;

import ao.decision.attr.Attribute;
import ao.decision.attr.AttributeSet;
import ao.decision.data.Context;
import ao.decision.data.DataSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 *
 */
public class DecisionLearner<T>
{
    //--------------------------------------------------------------------
    private DecisionTree<T> tree;


    //--------------------------------------------------------------------
    public void train(DataSet<T> ds)
    {
        tree = induce(ds, ds.contextAttributes());
        System.out.println(tree);
    }

    private DecisionTree<T> induce(
            DataSet<T>                  ds,
            Collection<AttributeSet<?>> attributes)
    {
        if (attributes.isEmpty())
        {
            return DecisionLeaf.newInstance(ds.frequencies());
        }
        AttributeSet<?>    splitOn   =
                chooseAttribute(ds, attributes);
        DecisionTree<T> splitTree =
                new DecisionTree<T>(splitOn);

        Collection<AttributeSet<?>> newAttribs =
                new ArrayList<AttributeSet<?>>(attributes);
        newAttribs.remove( splitOn );

        for (Map.Entry<Attribute, DataSet<T>> splitPlane :
                ds.split( splitOn ).entrySet())
        {
            DecisionTree<T> subTree =
                    induce(splitPlane.getValue(), newAttribs);
            splitTree.addNode(splitPlane.getKey(), subTree);
		}
        
        return splitTree;
    }

    private AttributeSet<?> chooseAttribute(
            DataSet<T>                  ds,
            Collection<AttributeSet<?>> attributes)
    {
        double          mml          = -1;
		AttributeSet<?> mmlAttribute = null;
		for (AttributeSet<?> attr : attributes)
        {
			double gain = ds.informationGain(attr);
			if (gain > mml)
            {
				mml = gain;
				mmlAttribute  = attr;
			}
		}
		return mmlAttribute;
    }


    //--------------------------------------------------------------------
    public Histogram<T> predict(Context context)
    {
        return tree.predict( context );
    }
}
