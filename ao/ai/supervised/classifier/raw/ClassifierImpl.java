package ao.ai.supervised.classifier.raw;

import ao.ai.supervised.classifier.processed.LocalClassifier;
import ao.ai.supervised.decision.classification.raw.Prediction;
import ao.ai.supervised.decision.input.processed.data.DataPool;
import ao.ai.supervised.decision.input.raw.example.Context;
import ao.ai.supervised.decision.input.raw.example.Example;
import ao.ai.supervised.decision.input.raw.example.LearningSet;

/**
 *
 */
public class ClassifierImpl implements Classifier
{
    //--------------------------------------------------------------------
    private final DataPool        pool;
    private final LocalClassifier deleget;


    //--------------------------------------------------------------------
    public ClassifierImpl(LocalClassifier localClassifier)
    {
        pool    = new DataPool();
        deleget = localClassifier;
    }


    //--------------------------------------------------------------------
    public void set(LearningSet ls)
    {
        deleget.set( ls.toLearningSet(pool) );
    }

    public void add(LearningSet ls)
    {
        deleget.add( ls.toLearningSet(pool) );
    }

    public void add(Example example)
    {
        deleget.add( example.toExample(pool) );
    }

    public Prediction classify(Context context)
    {
        return new Prediction(
                    deleget.classify(context.toContext(pool)),
                    pool);
    }


    //--------------------------------------------------------------------
    public void limitPopulation(int toMostRecent)
    {
        deleget.limitPopulation( toMostRecent );
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return deleget.toString();
    }
}
