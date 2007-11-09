package ao.ai.opp_model.classifier;

import ao.ai.opp_model.decision2.classification.Classification;
import ao.ai.opp_model.decision2.example.Context;
import ao.ai.opp_model.decision2.example.LearningSet;
import ao.ai.opp_model.decision2.data.DataPool;

/**
 *
 */
public interface Classifier
{
    public DataPool pool();

    public void train(LearningSet ls);

    public Classification classify(Context context);
}
