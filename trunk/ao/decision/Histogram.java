package ao.decision;

import ao.decision.attr.Attribute;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Histogram<T>
{
    //--------------------------------------------------------------------
    private Map<Attribute<T>, Integer> hist;
    private double                     total = 0;



    //--------------------------------------------------------------------
    public Histogram()
    {
        hist = new HashMap<Attribute<T>, Integer>();
    }


    //--------------------------------------------------------------------
    public void count(Attribute<T> attribute)
    {
        int count = hist.get(attribute);
        hist.put(attribute, ++count);
        total++;
    }

    //--------------------------------------------------------------------
    public double probabilityOf(Attribute<T> attribute)
    {
        return hist.get(attribute) / total;
    }


//    //--------------------------------------------------------------------
//    public T nextRandom()
//    {
//        double weightLeft = Rand.nextDouble( totalWeight );
//
//        for (int i = 0; i < weights.length; i++)
//        {
//            if ((weightLeft -= weights[i]) < 0)
//            {
//                return vals[ i ];
//            }
//        }
//
//        return null;
//    }


    //--------------------------------------------------------------------
    public Attribute<T> mostProbable()
    {
        Attribute<T> mostFrequent = null;
        int          count        = -1;

        for (Map.Entry<Attribute<T>, Integer> entry : hist.entrySet())
        {
            if (entry.getValue() > count)
            {
                count        = entry.getValue();
                mostFrequent = entry.getKey();
            }
        }
        return mostFrequent;
    }


//    //--------------------------------------------------------------------
//    public double[] weights()
//    {
//        return weights.clone();
//    }
//
//
//    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return hist.toString();
    }
}
