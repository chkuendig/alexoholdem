package ao.ai.supervised.classifier.raw;

import ao.ai.supervised.decision.input.raw.example.Example;
import ao.ai.supervised.decision.input.raw.example.LearningSet;

/**

 */
public interface Classifier extends Predictor
{
    public void set(LearningSet ls);
    public void add(LearningSet ls);
    public void add(Example example);

    public void limitPopulation(int toMostRecent);
}
