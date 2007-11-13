package ao.ai.opp_model.classifier;

import ao.ai.opp_model.decision.classification.Classification;
import ao.ai.opp_model.decision.example.Context;
import ao.ai.opp_model.decision.example.LearningSet;
import ao.ai.opp_model.decision.data.DataPool;

/**
 *
 */
public interface Classifier
{
    public DataPool pool();

    public void train(LearningSet ls);

    public Classification classify(Context context);
}
