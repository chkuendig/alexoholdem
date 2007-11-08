package ao.ai.opp_model;

import ao.ai.opp_model.decision2.Classifier;
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
