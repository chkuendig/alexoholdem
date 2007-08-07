package ao.holdem.bots.opp_model.predict.def.context;

import java.util.Arrays;

/**
 *
 */
public abstract class AbstractContext implements PredictionContext
{
    //--------------------------------------------------------------------
    private double inputs[];


    //--------------------------------------------------------------------
    public AbstractContext()
    {
        inputs = new double[ 0 ];
    }


    //--------------------------------------------------------------------
    protected void addNeuralInput(double... addends)
    {
        inputs =
                Arrays.copyOf(inputs,
                              inputs.length + addends.length);

        for (int i = 0; i < addends.length; i++)
        {
            inputs[ inputs.length - i - 1] = addends[i];
        }
    }


    //--------------------------------------------------------------------
    public double[] neuralInput()
    {
        return inputs;
    }

    public int neuralInputSize()
    {
        return inputs.length;
    }
}
