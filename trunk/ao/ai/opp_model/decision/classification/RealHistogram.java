package ao.ai.opp_model.decision.classification;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class RealHistogram<T>
{
    //--------------------------------------------------------------------
    private Map<T, double[]> hist;
    private int              count;
    private double           total;
    private int              sampleSize;


    //--------------------------------------------------------------------
    public RealHistogram()
    {
        this(0);
    }
    public RealHistogram(int givenSampleSize)
    {
        hist       = new HashMap<T, double[]>();
        sampleSize = givenSampleSize;
    }

    private RealHistogram(Map<T, double[]> copyHist,
                          int              copyCount,
                          double           copyTotal)
    {
        count = copyCount;
        total = copyTotal;
        hist  = new HashMap<T, double[]>( copyHist );

        for (T key : hist.keySet())
        {
            hist.put(key, hist.get(key).clone() );
        }
    }


    //--------------------------------------------------------------------
    public void incrementSampleSize()
    {
        sampleSize++;
    }
    public void setSampleSize(int newSampleSize)
    {
        sampleSize = newSampleSize;
    }
    public int sampleSize()
    {
        return sampleSize;
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
        return count == 0;
    }


    //--------------------------------------------------------------------
    public void add(T item, double value)
    {
        double oldVal[] = get( item );

        oldVal[0] += value;
        total     += value;
        count++;
    }
    public void add(T item)
    {
        add(item, 1.0);
    }


    //--------------------------------------------------------------------
    private double[] get(T item)
    {
        double count[] = hist.get( item );
        if (count == null)
        {
            count = new double[ 1 ];
            hist.put(item, count);
        }
        return count;
    }


    //--------------------------------------------------------------------
    public double valueOf(T item)
    {
        double value[] = hist.get( item );
        return (value == null)
                ? 0
                : value[ 0 ];
    }


    //--------------------------------------------------------------------
    public double probabilityOf(T item)
    {
        return isEmpty() || total == 0
                ? 0
                : valueOf( item ) / total;
    }

    public T mostProbable()
    {
        T      mostProbable  = null;
        double greatestValue = Long.MIN_VALUE;
        for (Map.Entry<T, double[]> entry : hist.entrySet())
        {
            if (entry.getValue()[0] > greatestValue)
            {
                mostProbable  = entry.getKey();
                greatestValue = entry.getValue()[0];
            }
        }
        return mostProbable;
    }


    //--------------------------------------------------------------------
    public RealHistogram<T> prototype()
    {
        return new RealHistogram<T>(hist, count, total);
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        StringBuilder str = new StringBuilder();

        for (Map.Entry<T, double[]> e : hist.entrySet())
        {
            str.append(e.getKey())
                    .append("\t")
                    .append(e.getValue()[0])
                    .append("\t");
        }
        str.deleteCharAt( str.length() - 1 );

        return str.toString();
    }
}
