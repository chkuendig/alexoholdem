package ao.ai.supervised.classifier.processed;

import ao.ai.supervised.decision.classification.processed.Classification;
import ao.ai.supervised.decision.input.processed.example.LocalContext;
import ao.ai.supervised.decision.input.processed.example.LocalExample;
import ao.ai.supervised.decision.input.processed.example.LocalLearningSet;

/**
 *
 */
public interface LocalClassifier
{
    //--------------------------------------------------------------------
    public void set(LocalLearningSet ls);
    public void add(LocalLearningSet ls);
    public void add(LocalExample example);

    public Classification classify(LocalContext context);

    public void limitPopulation(int toMostRecent);


    //--------------------------------------------------------------------
    public static interface Factory
    {
        public LocalClassifier newInstance();
    }
}
