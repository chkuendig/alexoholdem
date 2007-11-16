package ao.ai.opp_model.classifier;

import ao.ai.opp_model.decision.classification.Classification;
import ao.ai.opp_model.decision.data.DataPool;
import ao.ai.opp_model.decision.example.Context;
import ao.ai.opp_model.decision.example.Example;
import ao.ai.opp_model.decision.example.LearningSet;

/**
 *
 */
public interface Classifier
{
    public DataPool pool();

    public void set(LearningSet ls);
    public void add(LearningSet ls);
    public void add(Example     example);

    public Classification classify(Context context);
}
