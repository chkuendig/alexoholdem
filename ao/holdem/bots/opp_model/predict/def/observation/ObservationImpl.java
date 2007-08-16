package ao.holdem.bots.opp_model.predict.def.observation;

import java.util.Arrays;

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


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return Arrays.toString(values);
    }
}
