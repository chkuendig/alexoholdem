package ao.ai.opp_model.decision.input.processed.example;


import ao.ai.opp_model.decision.input.processed.attribute.Attribute;
import ao.ai.opp_model.decision.classification.processed.Classification;
import ao.ai.opp_model.decision.input.processed.data.LocalDatum;

import java.util.*;

/**
 *
 */
public class LocalLearningSet implements Iterable<LocalExample>
{
    //--------------------------------------------------------------------
    private List<LocalExample> data;
//    private Map<String, Attribute> byType;


    //--------------------------------------------------------------------
    public LocalLearningSet()
    {
        data   = new ArrayList<LocalExample>();
//        byType = new HashMap<String, Attribute>();
    }


    //--------------------------------------------------------------------
    public void add(LocalExample datum)
    {
        data.add( datum );
    }

    public int size()
    {
        return data.size();
    }

    public boolean isEmpty()
    {
        return data.isEmpty();
    }


    //--------------------------------------------------------------------
    public Classification classify()
    {
        Classification clazz =
                firstExample().target().attribute().newClassification();
        for (LocalExample e : data)
        {
            clazz.add( e.target() );
        }
        return clazz;
    }


    //--------------------------------------------------------------------
    public Iterator<LocalExample> iterator()
    {
        return data.iterator();
    }


    //--------------------------------------------------------------------
    public Map<LocalDatum, LocalLearningSet> split(Attribute on)
    {
        Map<LocalDatum, LocalLearningSet> split = new HashMap<LocalDatum, LocalLearningSet>();

        Collection<? extends LocalDatum> parts = on.partition();
        for (LocalExample e : data)
        {
            for (LocalDatum datum : parts)
            {
                if (datum.contains( e.datumOfType( on ) ))
                {
                    LocalLearningSet subSet = split.get(datum);
                    if (subSet == null)
                    {
                        subSet = new LocalLearningSet();
                        split.put(datum, subSet);
                    }
                    subSet.add( e );

                    break;
                }
            }
        }
        return split;
    }


    //--------------------------------------------------------------------
    public void addAll(LocalLearningSet addend)
    {
        data.addAll( addend.data );
    }


    //--------------------------------------------------------------------
    public List<Attribute> contextAttributes()
    {
        return isEmpty()
               ? null
               : firstExample().dataAttributes();
    }

    public Attribute targetAttribute()
    {
        return isEmpty()
               ? null
               : firstExample().targetAttribute();
    }

    private LocalExample firstExample()
    {
        return data.get(0);
    }
}

