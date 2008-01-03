package ao.ai.opp_model.classifier.raw;

import ao.ai.opp_model.decision.classification.raw.Prediction;
import ao.ai.opp_model.decision.input.raw.example.Context;

/**
 *
 */
public interface Predictor
{
    public Prediction classify(Context context);
}
