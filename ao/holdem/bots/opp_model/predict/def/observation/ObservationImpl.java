package ao.holdem.bots.opp_model.predict.def.observation;

import ao.util.text.Arr;

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
        return Arr.join(values, "\t");
    }
}
