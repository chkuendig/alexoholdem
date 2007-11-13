package ao.ai.opp_model.decision.example;


import ao.ai.opp_model.decision.attribute.Attribute;
import ao.ai.opp_model.decision.classification.Classification;
import ao.ai.opp_model.decision.data.Datum;

import java.util.*;

/**
 *
 */
public class LearningSet implements Iterable<Example>
{
    //--------------------------------------------------------------------
    private List<Example> data;
//    private Map<String, Attribute> byType;


    //--------------------------------------------------------------------
    public LearningSet()
    {
        data   = new ArrayList<Example>();
//        byType = new HashMap<String, Attribute>();
    }


    //--------------------------------------------------------------------
    public void add(Example datum)
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
                Classification.forType(
                        firstExample().target().attribute());
        for (Example e : data)
        {
            clazz.add( e.target() );
        }
        return clazz;
    }


    //--------------------------------------------------------------------
    public Iterator<Example> iterator()
    {
        return data.iterator();
    }


    //--------------------------------------------------------------------
    public Map<Datum, LearningSet> split(Attribute on)
    {
        Map<Datum, LearningSet> split = new HashMap<Datum, LearningSet>();

        Collection<? extends Datum> parts = on.partition();
        for (Example e : data)
        {
            for (Datum datum : parts)
            {
                if (datum.contains( e.datumOfType( on ) ))
                {
                    LearningSet subSet = split.get(datum);
                    if (subSet == null)
                    {
                        subSet = new LearningSet();
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
    public void addAll(LearningSet addend)
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

    private Example firstExample()
    {
        return data.get(0);
    }
}

