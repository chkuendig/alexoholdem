package ao.ai.opp_model.mix;

import ao.util.rand.Rand;

import java.io.Serializable;
import java.util.Arrays;

/**
 *
 */
public class Classification<T extends Enum>
        implements Serializable
{
    //--------------------------------------------------------------------
    private double totalWeight = 0;
    private double weights[];
    private T      vals[];


    //--------------------------------------------------------------------
    public Classification(Class<T> enumType)
    {
        vals    = enumType.getEnumConstants();
        weights = new double[ vals.length ];
    }


    //--------------------------------------------------------------------
    public Classification<T> add(T element, double weight)
    {
        assert weight >= 0;

        int index = element.ordinal();

        double prevVal = weights[ index ];
        weights[ index ] = weight;

        totalWeight += weight - prevVal;

        return this;
    }
    

    //--------------------------------------------------------------------
    public double probabilityOf(T element)
    {
        int index = element.ordinal();
        return weights[ index ] / totalWeight;
    }


    //--------------------------------------------------------------------
    public T nextRandom()
    {
        double weightLeft = Rand.nextDouble( totalWeight );

        for (int i = 0; i < weights.length; i++)
        {
            if ((weightLeft -= weights[i]) < 0)
            {
                return vals[ i ];
            }
        }

        return null;
    }


    //--------------------------------------------------------------------
    public T mostProbable()
    {
        T      heaviestValue  = null;
        double heaviestWeight = -99999999999999D;

        for (int i = 0; i < weights.length; i++)
        {
            if (heaviestWeight < weights[i])
            {
                heaviestWeight = weights[i];
                heaviestValue  = vals[i];
            }
        }

        return heaviestValue;
    }


    //--------------------------------------------------------------------
    public double[] weights()
    {
        return weights.clone();
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return Arrays.toString( weights );
    }
}
