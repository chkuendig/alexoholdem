package ao.ai.opp_model.decision.classification;

import java.util.*;

/**
 *
 */
public class ConfusionMatrix<T>
{
    //--------------------------------------------------------------------
    private Map<T, Histogram<T>> matrix;
    private Set<T>               all;


    //--------------------------------------------------------------------
    public ConfusionMatrix()
    {
        all    = new HashSet<T>();
        matrix = new HashMap<T, Histogram<T>>();
    }


    //--------------------------------------------------------------------
    public void add(T actual, T predicted)
    {
        if (actual == null || predicted == null) return;

        all.add(actual);
        all.add(predicted);
        getPredicted(actual).add( predicted );
    }


    //--------------------------------------------------------------------
    private Histogram<T> getPredicted(T forActual)
    {
        Histogram<T> predicted = matrix.get( forActual );
        if (predicted == null)
        {
            predicted = new Histogram<T>();
            matrix.put(forActual, predicted);
        }
        return predicted;
    }


    //--------------------------------------------------------------------
    public T adjust(Histogram<T> hist)
    {
        double minDistance = Double.MAX_VALUE;
        T      closest     = null;

        for (Map.Entry<T, Histogram<T>> entry : matrix.entrySet())
        {
            double dist = hist.distance( entry.getValue() );
            if (dist < minDistance)
            {
                closest     = entry.getKey();
                minDistance = dist;
            }
        }
        
        return closest;
    }


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public String toString()
    {
        if (all.isEmpty()) return "[]";

        Collection<T> allItems;

        T firstItem = all.iterator().next();
        if (firstItem instanceof Comparable)
        {
            allItems = new TreeSet( all );
        }
        else
        {
            allItems = all;
        }

        StringBuilder str = new StringBuilder();

        str.append("actual\tpredicted\n");
        for (T actualItem : allItems)
        {
            for (T predictedItem : allItems)
            {
                int count = getPredicted(actualItem)
                                .countOf( predictedItem );

                str.append(actualItem)
                   .append("\t")
                   .append(predictedItem)
                   .append("\t")
                   .append(count)
                   .append("\n");
            }
        }

        return str.toString();
    }
}
