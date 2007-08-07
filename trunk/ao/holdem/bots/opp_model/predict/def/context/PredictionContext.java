package ao.holdem.bots.opp_model.predict.def.context;

/**
 *
 */
public interface PredictionContext
{
    public double[] neuralInput();
    public int      neuralInputSize();
}
