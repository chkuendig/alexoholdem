package ao.ai.opp_model.classifier.raw;

import ao.ai.opp_model.classifier.processed.LocalClassifier;
import ao.ai.opp_model.decision.classification.raw.Prediction;
import ao.ai.opp_model.decision.input.processed.data.DataPool;
import ao.ai.opp_model.decision.input.raw.example.Context;
import ao.ai.opp_model.decision.input.raw.example.Example;
import ao.ai.opp_model.decision.input.raw.example.LearningSet;

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
    public String toString()
    {
        return deleget.toString();
    }
}
