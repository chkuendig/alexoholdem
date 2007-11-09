package ao.ai.opp_model.decision2.tree;

import ao.ai.opp_model.classifier.ClassifierImpl;
import ao.ai.opp_model.decision2.attribute.Attribute;
import ao.ai.opp_model.decision2.classification.Classification;
import ao.ai.opp_model.decision2.example.Context;
import ao.ai.opp_model.decision2.example.LearningSet;

/**
 *
 */
public class GeneralTreeLearner extends ClassifierImpl
{
    //--------------------------------------------------------------------
    private GeneralTree tree;

    
    //--------------------------------------------------------------------
    public synchronized void train(LearningSet ls)
    {
        if (ls.isEmpty()) return;

        tree = induce(ls);
//        tree = new DecisionTree<T>(ds);
//        induce(ds.contextAttributes(), tree);

//        System.out.println(tree + "" + tree.messageLength());
    }

    private GeneralTree induce(LearningSet ls)
    {
        GeneralTree root          = new GeneralTree(ls);
        double      messageLength = root.messageLength();

        while (true)
        {
            double      mml     = Double.MAX_VALUE;
            Attribute   mmlAttr = null;
            GeneralTree mmlLeaf = null;
            for (GeneralTree leaf : root.leaves())
            {
                for (Attribute attr : leaf.availableAttributes())
                {
                    for (Attribute attrView : attr.views())
                    {
                        leaf.split(attrView);

                        double tentativeLength = root.messageLength();
//                        System.out.println(root + "" +
//                                           tentativeLength + "\n");
                        if (mml > tentativeLength)
                        {
                            mml     = tentativeLength;
                            mmlAttr = attrView;
                            mmlLeaf = leaf;
                        }

                        leaf.unsplit();
                    }
                }
            }

            if (messageLength > mml)
            {
                assert mmlLeaf != null; // to get rid of warning

                messageLength = mml;
                mmlLeaf.split( mmlAttr );
//                System.out.println("MADE CHOICE!!!!!");
//                System.out.println(root);
            }
            else break;
        }

//        root.freeze();
        return root;
    }


    //--------------------------------------------------------------------
    public Classification classify(Context context)
    {
        return (tree == null ?
                null : tree.classify( context ));
    }

    
    //--------------------------------------------------------------------
    public String toString()
    {
        return (tree == null)
                ? "No Tree Learned So Far"
                : tree.toString() + " " + tree.messageLength();
    }
}
