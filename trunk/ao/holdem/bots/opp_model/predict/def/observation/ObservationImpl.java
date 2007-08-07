package ao.holdem.bots.opp_model.predict.def.observation;

/**
 *
 */
public class ObservationImpl implements Observation
{
    //--------------------------------------------------------------------
    private double values[];


    //--------------------------------------------------------------------
    public ObservationImpl(double observed[])
    {
        values = observed;
    }


    //--------------------------------------------------------------------
    public double[] neuralOutput()
    {
        return values;
    }

    public int neuralOutputSize()
    {
        return values.length;
    }
}
