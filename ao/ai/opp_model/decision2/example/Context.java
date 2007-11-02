package ao.ai.opp_model.decision2.example;

import ao.ai.opp_model.decision2.attribute.Attribute;
import ao.ai.opp_model.decision2.data.Datum;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 *
 */
public class Context
{
    //--------------------------------------------------------------------
    private Map<String, Datum> byType = new HashMap<String, Datum>();


    //--------------------------------------------------------------------
    public Context() {}

    protected Context(Context copyContext)
    {
        byType.putAll( copyContext.byType );
    }

    public Context(Collection<? extends Datum> data)
    {
        for (Datum datum : data) add(datum);
    }


    //--------------------------------------------------------------------
    public Example withTarget(Datum target)
    {
        return new Example(this, target);
    }


    //--------------------------------------------------------------------
    public Collection<Attribute> attributes()
    {
        Collection<Attribute> attributes = new ArrayList<Attribute>();
        for (Datum datum : byType.values())
        {
            attributes.add( datum.attribute() );
        }
        return attributes;
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
