package ao.ai.supervised.classifier.raw;

import ao.ai.supervised.decision.classification.raw.Prediction;
import ao.ai.supervised.decision.input.raw.example.Context;

/**
 *
 */
public interface Predictor
{
    public Prediction classify(Context context);
}
