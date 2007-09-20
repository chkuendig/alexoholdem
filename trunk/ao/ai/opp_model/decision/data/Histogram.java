package ao.ai.opp_model.decision.data;

import ao.ai.opp_model.decision.attr.Attribute;
import ao.util.stats.Info;

import java.util.Collection;
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
    public int numClasses()
    {
        return hist.size();
    }

    public int total()
    {
        return (int) total;
    }

    public Collection<Attribute<T>> attributes()
    {
        return hist.keySet();
    }


    //--------------------------------------------------------------------
    public void add(Attribute<T> attribute)
    {
        Integer count = hist.get(attribute);
        hist.put(attribute,
                 (count == null ? 1 : ++count));
        total++;
    }

    //--------------------------------------------------------------------
    public double probabilityOf(T attributeValue)
    {
        return probabilityOf(attributeOf(attributeValue));
    }
    public double probabilityOf(Attribute<T> attribute)
    {
        return (attribute == null)
                ? 0 : countOf(attribute) / total;
    }

    public int countOf(T attribute)
    {
        return countOf(attributeOf(attribute));
    }
    public int countOf(Attribute<T> attribute)
    {
        Integer count = hist.get(attribute);
        return count == null ? 0 : count;
    }

    private Attribute<T> attributeOf(T value)
    {
        for (Attribute<T> attr : hist.keySet())
        {
            if (attr.value().equals( value ))
            {
                return attr;
            }
        }
        return null;
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

    
    //--------------------------------------------------------------------
//    public MixedAction toMixedAction()
//    {
//        double fold = Stats.accountForStatisticalError(
//
//        );
//
//        return null;
//    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return hist.toString() + "\t" +
               Info.cost(probabilityOf(mostProbable()));
    }
}
