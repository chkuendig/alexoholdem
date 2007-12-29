package ao.ai.opp_model.decision.input.processed.attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class SortedList<T extends Comparable<T>>
{
    //--------------------------------------------------------------------
    private List<T> sorted;


    //--------------------------------------------------------------------
    public SortedList()
    {
        sorted = new ArrayList<T>();
    }


    //--------------------------------------------------------------------
    public void addIfAbsent(T value)
    {
        int place = Collections.binarySearch(sorted, value);
        if (place < 0)
        {
            // place = (-(insertion point) - 1)
            // place + 1 = -(insertion point)
            // insertion point = -(place + 1)
            int insertAt = -(place + 1);
            sorted.add(insertAt, value);
        }
    }

    
    //--------------------------------------------------------------------
    public T get(int index)
    {
        return sorted.get(index);
    }

    public T getPercentileUp(double percentile)
    {
        return sorted.get( percentileUp(percentile) );
    }
    public T getPercentileDown(double percentile)
    {
        return sorted.get( percentileDown(percentile) );
    }


    //--------------------------------------------------------------------
    public int percentileUp(double percentile)
    {
        return (int) Math.ceil((size()-1) * percentile);
    }
    public int percentileDown(double percentile)
    {
        return (int) Math.floor((size()-1) * percentile);
    }

    public int size()
    {
        return sorted.size();
    }
}
