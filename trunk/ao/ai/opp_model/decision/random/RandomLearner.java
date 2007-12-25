package ao.ai.opp_model.decision.random;

import ao.ai.opp_model.classifier.processed.LocalClassifier;
import ao.ai.opp_model.classifier.processed.LocalClassifierFactory;
import ao.ai.opp_model.decision.classification.processed.Classification;
import ao.ai.opp_model.decision.classification.processed.Frequency;
import ao.ai.opp_model.decision.input.processed.attribute.Multistate;
import ao.ai.opp_model.decision.input.processed.data.LocalDatum;
import ao.ai.opp_model.decision.input.processed.example.LocalContext;
import ao.ai.opp_model.decision.input.processed.example.LocalExample;
import ao.ai.opp_model.decision.input.processed.example.LocalLearningSet;
import org.jetbrains.annotations.NotNull;

/**
 * 
 */
public class RandomLearner implements LocalClassifier
{
    //--------------------------------------------------------------------
    public static LocalClassifierFactory FACTORY =
            new LocalClassifierFactory() {
                public LocalClassifier newInstance() {
                    return new RandomLearner();
                }
            };

    private static final int NUM_TREES = 32;


    //--------------------------------------------------------------------
    private RandomTree trees[];
    private Multistate targetAttribute;


    //--------------------------------------------------------------------
    public synchronized void set(LocalLearningSet ls)
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
    public void add(LocalLearningSet ls)
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
    public void add(@NotNull LocalExample example)
    {
//        if (example == null) return;

        if (needsInitiation())
        {
            LocalLearningSet s = new LocalLearningSet();
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
    private void doAdd(LocalLearningSet ls)
    {
        for (LocalExample example : ls)
        {
            doAdd( example );
        }
    }

    private void doAdd(LocalExample example)
    {
        for (RandomTree tree : trees)
        {
            tree.updateSatistic(example);
        }
    }


    //--------------------------------------------------------------------
    public Classification classify(LocalContext context)
    {
        Frequency classification = new Frequency();
        if (targetAttribute == null) return classification;

        for (LocalDatum datum : targetAttribute.partition())
        {
            double actSum = 0;
            for (RandomTree tree : trees)
            {
                actSum += tree.proportionAtLeaf(context, datum);
            }
            classification.put(
                    datum, (int)(100 * (actSum / trees.length)));
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
