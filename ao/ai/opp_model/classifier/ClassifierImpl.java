package ao.ai.opp_model.classifier;

import ao.ai.opp_model.classifier.Classifier;
import ao.ai.opp_model.decision2.data.DataPool;

/**
 *
 */
public abstract class ClassifierImpl implements Classifier
{
    //--------------------------------------------------------------------
    private DataPool pool = new DataPool();


    //--------------------------------------------------------------------
    public DataPool pool()
    {
        return pool;
    }
}
