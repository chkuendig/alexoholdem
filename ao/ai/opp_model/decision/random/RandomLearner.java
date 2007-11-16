package ao.ai.opp_model.decision.random;

import ao.ai.opp_model.classifier.ClassifierImpl;
import ao.ai.opp_model.decision.attribute.Multistate;
import ao.ai.opp_model.decision.classification.Classification;
import ao.ai.opp_model.decision.classification.Frequency;
import ao.ai.opp_model.decision.data.Datum;
import ao.ai.opp_model.decision.example.Context;
import ao.ai.opp_model.decision.example.Example;
import ao.ai.opp_model.decision.example.LearningSet;

/**
 * 
 */
public class RandomLearner extends ClassifierImpl
{
    //--------------------------------------------------------------------
    private static final int NUM_TREES = 32;


    //--------------------------------------------------------------------
    private RandomTree trees[];
    private Multistate targetAttribute;


    //--------------------------------------------------------------------
    public synchronized void set(LearningSet ls)
    {
        trees = new RandomTree[ NUM_TREES ];
        for (int i = 0; i < trees.length; i++)
        {
            trees[ i ] = RandomTree.nextRandom(ls);
        }

        if (ls.isEmpty())
        {
            targetAttribute = null;
            return;
        }
        else
        {
            assert (ls.targetAttribute() instanceof Multistate);
            targetAttribute = (Multistate) ls.targetAttribute();
        }

        doAdd( ls );
    }


    //--------------------------------------------------------------------
    public void add(LearningSet ls)
    {
        if (needsInitiation())
        {
            set( ls );
        }
        else if (! ls.isEmpty())
        {
            assert ls.targetAttribute().getClass() ==
                        targetAttribute.getClass();
            doAdd( ls );
        }
    }


    //--------------------------------------------------------------------
    public void add(Example example)
    {
        if (needsInitiation())
        {
            LearningSet s = new LearningSet();
            s.add( example );
            set( s );
        }
        else
        {
            assert example.targetAttribute().getClass() ==
                    targetAttribute.getClass();
            doAdd( example );
        }
    }

    private boolean needsInitiation()
    {
         return trees           == null ||
                targetAttribute == null;
    }


    //--------------------------------------------------------------------
    private void doAdd(LearningSet ls)
    {
        for (Example example : ls)
        {
            doAdd( example );
        }
    }

    private void doAdd(Example example)
    {
        for (RandomTree tree : trees)
        {
            tree.updateSatistic(example);
        }
    }


    //--------------------------------------------------------------------
    public Classification classify(Context context)
    {
        Frequency classification = new Frequency();
        if (targetAttribute == null) return classification;

        for (Datum datum : targetAttribute.partition())
        {
            double actSum = 0;
            for (RandomTree tree : trees)
            {
                actSum += tree.proportionAtLeaf(context, datum);
            }
            classification.put(
                    datum, (int)(1000 * (actSum / trees.length)));
        }
        return classification;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return "RandomLearner with " + trees.length + " trees";
    }

    public void printAsForest()
    {
        for (int i = 0; i < trees.length; i++)
        {
            System.out.println(i + ": " + trees[ i ]);
        }
    }
}
