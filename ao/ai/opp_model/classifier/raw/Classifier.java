package ao.ai.opp_model.classifier.raw;

import ao.ai.opp_model.decision.classification.raw.Prediction;
import ao.ai.opp_model.decision.input.raw.example.Context;
import ao.ai.opp_model.decision.input.raw.example.Example;
import ao.ai.opp_model.decision.input.raw.example.LearningSet;

/**

 */
public interface Classifier
{
    public void set(LearningSet ls);
    public void add(LearningSet ls);
    public void add(Example example);

    public Prediction classify(Context context);
}
