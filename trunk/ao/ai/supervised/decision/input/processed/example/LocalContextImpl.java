package ao.ai.supervised.decision.input.processed.example;

import ao.ai.supervised.decision.input.processed.attribute.Attribute;
import ao.ai.supervised.decision.input.processed.data.LocalDatum;

import java.util.*;

/**
 *
 */
public class LocalContextImpl implements LocalContext
{
    //--------------------------------------------------------------------
    private Map<String, LocalDatum> byType =
            new HashMap<String, LocalDatum>();


    //--------------------------------------------------------------------
    public LocalContextImpl() {}

    public LocalContextImpl(LocalContext copyContext)
    {
        this( copyContext.data() );
    }

    protected LocalContextImpl(LocalContextImpl copyContext)
    {
        byType.putAll( copyContext.byType );
    }

    public LocalContextImpl(Collection<? extends LocalDatum> data)
    {
        for (LocalDatum datum : data) add(datum);
    }


    //--------------------------------------------------------------------
    public LocalExample withTarget(LocalDatum target)
    {
        return new LocalExampleImpl(this, target);
    }


    //--------------------------------------------------------------------
    public List<Attribute> dataAttributes()
    {
        List<Attribute> attributes = new ArrayList<Attribute>();
        for (LocalDatum datum : byType.values())
        {
            attributes.add( datum.attribute() );
        }
        return attributes;
    }

    public Collection<LocalDatum> data()
    {
        return byType.values();
    }


    //--------------------------------------------------------------------
    public void add(LocalDatum datum)
    {
        byType.put(datum.attribute().type(), datum);
    }

    public void addAll(LocalContext dataFrom)
    {
        for (LocalDatum datum : dataFrom.data())
        {
            add( datum );
        }
    }


    //--------------------------------------------------------------------
    public LocalDatum datumOfType(Attribute attribute)
    {
        return byType.get( attribute.type() );
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return byType.toString();
    }
}
