package ao.ai.opp_model.predict.def.observation;

/**
 *
 */
public interface Observation
{
    public double[] neuralOutput();
    public int      neuralOutputSize();
}
