package ao.ai.opp_model.decision.example;

import ao.ai.opp_model.decision.attribute.Attribute;
import ao.ai.opp_model.decision.data.Datum;

import java.util.*;

/**
 *
 */
public class ContextImpl implements Context
{
    //--------------------------------------------------------------------
    private Map<String, Datum> byType = new HashMap<String, Datum>();


    //--------------------------------------------------------------------
    public ContextImpl() {}

    protected ContextImpl(ContextImpl copyContext)
    {
        byType.putAll( copyContext.byType );
    }

    public ContextImpl(Collection<? extends Datum> data)
    {
        for (Datum datum : data) add(datum);
    }


    //--------------------------------------------------------------------
    public Example withTarget(Datum target)
    {
        return new ExampleImpl(this, target);
    }


    //--------------------------------------------------------------------
    public List<Attribute> dataAttributes()
    {
        List<Attribute> attributes = new ArrayList<Attribute>();
        for (Datum datum : byType.values())
        {
            attributes.add( datum.attribute() );
        }
        return attributes;
    }

    public Collection<Datum> data()
    {
        return byType.values();
    }


    //--------------------------------------------------------------------
    public void add(Datum datum)
    {
        byType.put(datum.attribute().type(), datum);
    }


    //--------------------------------------------------------------------
    public Datum datumOfType(Attribute attribute)
    {
        return byType.get( attribute.type() );
    }
}
