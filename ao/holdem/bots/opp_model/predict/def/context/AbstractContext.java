package ao.holdem.bots.opp_model.predict.def.context;

import javax.persistence.Entity;
import java.util.Arrays;

/**
 *
 */
@Entity
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
    public Class<? extends PredictionContext> predictionType()
    {
        return getClass();
    }


    //--------------------------------------------------------------------
    public double[] getInputs()
    {
        return inputs;
    }

    public void setInputs(double[] inputs)
    {
        this.inputs = inputs;
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
