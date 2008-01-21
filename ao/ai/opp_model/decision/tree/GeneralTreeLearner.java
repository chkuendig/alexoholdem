package ao.ai.opp_model.decision.tree;

import ao.ai.opp_model.classifier.processed.LocalClassifier;
import ao.ai.opp_model.decision.classification.processed.Classification;
import ao.ai.opp_model.decision.input.processed.attribute.Attribute;
import ao.ai.opp_model.decision.input.processed.example.LocalContext;
import ao.ai.opp_model.decision.input.processed.example.LocalExample;
import ao.ai.opp_model.decision.input.processed.example.LocalLearningSet;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class GeneralTreeLearner implements LocalClassifier
{
    //--------------------------------------------------------------------
    public static class Factory implements LocalClassifier.Factory
    {
        public LocalClassifier newInstance()
        {
            return new GeneralTreeLearner();
        }
    }


    //--------------------------------------------------------------------
    private GeneralTree      tree;
    private LocalLearningSet examples;


    //--------------------------------------------------------------------
    public synchronized void set(LocalLearningSet ls)
    {
        examples = ls;
        if (ls.isEmpty()) return;

        tree = induce(ls);
//        tree = new DecisionTree<T>(ds);
//        induce(ds.contextAttributes(), tree);

//        System.out.println(tree + "" + tree.messageLength());
    }

    public void add(LocalLearningSet ls)
    {
        examples.addAll( ls );
        set( examples );
    }

    public void add(@NotNull LocalExample example)
    {
        LocalLearningSet s = new LocalLearningSet();
        s.add( example );
        add( s );
    }


    //--------------------------------------------------------------------
    private GeneralTree induce(LocalLearningSet ls)
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
    public Classification classify(LocalContext context)
    {
        return (tree == null ?
                null : tree.classify( context ));
    }


    //--------------------------------------------------------------------
    public void limitPopulation(int toMostRecent)
    {
        examples.forget(toMostRecent);
        set( examples );
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return (tree == null)
                ? "No Tree Learned So Far"
                : tree.toString() + " " + tree.messageLength();
    }
}
