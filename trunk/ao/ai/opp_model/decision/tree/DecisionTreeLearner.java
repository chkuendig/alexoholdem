package ao.ai.opp_model.decision.tree;

import ao.ai.opp_model.decision.AbstractDecisionLearner;
import ao.ai.opp_model.decision.attr.AttributeSet;
import ao.ai.opp_model.decision.data.Context;
import ao.ai.opp_model.decision.data.DataSet;
import ao.ai.opp_model.decision.data.Histogram;

/**
 *
 */
public class DecisionTreeLearner<T> extends AbstractDecisionLearner<T>
{
    //--------------------------------------------------------------------
    private DecisionTree<T> tree;


    //--------------------------------------------------------------------
    public synchronized void train(DataSet<T> ds)
    {
        if (ds.isEmpty()) return;

        tree = induce(ds);
//        tree = new DecisionTree<T>(ds);
//        induce(ds.contextAttributes(), tree);

        System.out.println(tree);
    }

    private DecisionTree<T> induce(DataSet<T> ds)
    {
        DecisionTree<T> root = new DecisionTree<T>(ds);
        double messageLength = root.messageLength();

        while (true)
        {
            double          mml     = Double.MAX_VALUE;
            AttributeSet<?> mmlAttr = null;
            DecisionTree<T> mmlLeaf = null;
            for (DecisionTree<T> leaf : root.leafs())
            {
                for (AttributeSet<?> attr : leaf.availableContexts())
                {
                    leaf.split(attr);

                    double tentativeLength = root.messageLength();
                    if (mml > tentativeLength)
                    {
                        mml     = tentativeLength;
                        mmlAttr = attr;
                        mmlLeaf = leaf;
                    }

                    leaf.unsplit();
                }
            }

            if (messageLength > mml)
            {
                assert mmlLeaf != null; // to get rid of warning

                messageLength = mml;
                mmlLeaf.split( mmlAttr );
            }
            else break;
        }

        root.freeze();
        return root;
    }


//    private void induce(
//            Collection<AttributeSet<?>> attributes,
//            DecisionTree<T>             into)
//    {
//        if (attributes.isEmpty()) return;
//
//        AttributeSet<?> splitOn   =
//                chooseAttribute(into.trainingData(), attributes);
//        into.split( splitOn );
//
//        Collection<AttributeSet<?>> newAttribs =
//                new ArrayList<AttributeSet<?>>(attributes);
//        newAttribs.remove( splitOn );
//
//        for (DecisionTree<T> kid : into.kids())
//        {
//            induce(newAttribs, kid);
//        }
//    }

//    private AttributeSet<?> chooseAttribute(
//            DataSet<T>                  ds,
//            Collection<AttributeSet<?>> attributes)
//    {
//        double          maxInfoGain          = -1;
//		AttributeSet<?> maxInfoGainAttribute = null;
//		for (AttributeSet<?> attr : attributes)
//        {
//            double messageLength = ds.informationGain(attr);
//			if (messageLength > maxInfoGain)
//            {
//				maxInfoGain          = messageLength;
//				maxInfoGainAttribute = attr;
//			}
//		}
//		return maxInfoGainAttribute;
//    }


    //--------------------------------------------------------------------
    public Histogram<T> predict(Context context)
    {
        return (tree == null ?
                null : tree.predict( context ));
    }
}
