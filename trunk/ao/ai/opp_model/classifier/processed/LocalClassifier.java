package ao.ai.opp_model.classifier.processed;

import ao.ai.opp_model.decision.classification.processed.Classification;
import ao.ai.opp_model.decision.input.processed.example.LocalContext;
import ao.ai.opp_model.decision.input.processed.example.LocalExample;
import ao.ai.opp_model.decision.input.processed.example.LocalLearningSet;

/**
 *
 */
public interface LocalClassifier
{
    public void set(LocalLearningSet ls);
    public void add(LocalLearningSet ls);
    public void add(LocalExample example);

    public Classification classify(LocalContext context);
}
