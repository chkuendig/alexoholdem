package ao.ai.opp_model.classifier.raw;

import ao.ai.opp_model.decision.input.raw.example.Example;
import ao.ai.opp_model.decision.input.raw.example.LearningSet;

/**

 */
public interface Classifier extends Predictor
{
    public void set(LearningSet ls);
    public void add(LearningSet ls);
    public void add(Example example);

    public void limitPopulation(int toMostRecent);
}
