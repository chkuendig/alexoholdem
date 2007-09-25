package ao.ai.opp_model.decision;

import ao.ai.opp_model.decision.attr.AttributePool;

/**
 *
 */
public abstract class AbstractDecisionLearner<T>
        implements DecisionLearner<T>
{
    //--------------------------------------------------------------------
    private AttributePool pool = new AttributePool();


    //--------------------------------------------------------------------
    public AttributePool pool()
    {
        return pool;
    }
}
