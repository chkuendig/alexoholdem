package ao.decision;

import ao.decision.attr.AttributeSet;
import ao.decision.data.Context;
import ao.decision.data.DataSet;

/**
 *
 */
public class DecisionLearner<T>
{
    //--------------------------------------------------------------------
    private DecisionTree<T> tree;


    //--------------------------------------------------------------------
    public synchronized void train(DataSet<T> ds)
    {
        tree = induce(ds);
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
                for (AttributeSet<?> attr : leaf.unsplitContexts())
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
                messageLength = mml;
                mmlLeaf.split( mmlAttr );
            }
            else break;
        }

        root.freeze();
        return root;
    }


//    private DecisionTree<T> induce(
//            DataSet<T>                  ds,
//            Collection<AttributeSet<?>> attributes,
//            DecisionTree<T>             addTo)
//    {
//        if (attributes.isEmpty())
//        {
//            return DecisionLeaf.newInstance(ds.frequencies());
//        }
//        AttributeSet<?>    splitOn   =
//                chooseAttribute(ds, attributes, addTo);
//        DecisionTree<T> splitTree =
//                new DecisionTree<T>(splitOn);
//
//        Collection<AttributeSet<?>> newAttribs =
//                new ArrayList<AttributeSet<?>>(attributes);
//        newAttribs.remove( splitOn );
//
//        for (Map.Entry<Attribute, DataSet<T>> splitPlane :
//                ds.split( splitOn ).entrySet())
//        {
//            DecisionTree<T> subTree =
//                    induce(splitPlane.getValue(), newAttribs);
//            splitTree.addNode(splitPlane.getKey(), subTree);
//		}
//
//        return splitTree;
//    }

//    private AttributeSet<?> chooseAttribute(
//            DataSet<T>                  ds,
//            Collection<AttributeSet<?>> attributes,
//            DecisionTree<T>             parent)
//    {
//        double          mml          = -1;
//		AttributeSet<?> mmlAttribute = null;
//		for (AttributeSet<?> attr : attributes)
//        {
//            double treeLength = 0;
//            if (root == null)
//            {
//                treeLength = new DecisionTree<T>(attr)
//                                    .codingComplexity(1, 0);
//            }
//            else
//            {
//                parent.addNode(attr, new DecisionTree<T>(attr));
//
//                treeLength =
//            }
//
//            double messageLength = ds.informationGain(attr);
//			if (messageLength > mml)
//            {
//				mml = messageLength;
//				mmlAttribute  = attr;
//			}
//		}
//		return mmlAttribute;
//    }


    //--------------------------------------------------------------------
    public Histogram<T> predict(Context context)
    {
        return tree.predict( context );
    }
}
