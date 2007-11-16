package ao.ai.opp_model.decision.classification;

import ao.util.stats.Info;

import java.util.*;

/**
 *
 */
public class Histogram<T>
{
    //--------------------------------------------------------------------
    private Map<T, int[]> hist;
    private int           total;


    //--------------------------------------------------------------------
    public Histogram()
    {
        hist = new HashMap<T, int[]>();
    }


    //--------------------------------------------------------------------
    public Collection<T> classes()
    {
        return hist.keySet();
    }

    public int classCount()
    {
        return hist.size();
    }

    public boolean isEmpty()
    {
        return total == 0;
    }


    //--------------------------------------------------------------------
    public void put(T item, int count)
    {
        int oldVal[] = get( item );

        total     += count - oldVal[0];
        oldVal[0]  = count;
    }


    //--------------------------------------------------------------------
    public void add(T item)
    {
        int count[] = get( item );

        count[0]++;
        total++;
    }

    private int[] get(T item)
    {
        int count[] = hist.get( item );
        if (count == null)
        {
            count = new int[ 1 ];
            hist.put(item, count);
        }
        return count;
    }


    //--------------------------------------------------------------------
    public int countOf(T item)
    {
        int count[] = hist.get( item );
        return (count == null)
                ? 0
                : count[ 0 ];
    }


    //--------------------------------------------------------------------
    public double probabilityOf(T item)
    {
        return (double) countOf( item ) / total;
    }

    public T mostFrequent()
    {
        T   mostFrequent  = null;
        int greatestCount = Integer.MIN_VALUE;
        for (Map.Entry<T, int[]> entry : hist.entrySet())
        {
            if (entry.getValue()[0] > greatestCount)
            {
                mostFrequent  = entry.getKey();
                greatestCount = entry.getValue()[0];
            }
        }
        return mostFrequent;
    }

    
    //--------------------------------------------------------------------
    public double distance(Histogram<T> histogram)
    {
        Set<T> mutual = new HashSet<T>();
        mutual.addAll(           hist.keySet() );
        mutual.addAll( histogram.hist.keySet() );

        double distance = 0;
        for (T item : mutual)
        {
            double deltaProb =
                    probabilityOf( item ) -
                    histogram.probabilityOf( item );
            distance -= Info.log2(1.0 - Math.abs(deltaProb));
        }
        return distance;
    }
}
