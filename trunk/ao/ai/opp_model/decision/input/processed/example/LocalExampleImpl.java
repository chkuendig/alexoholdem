package ao.ai.opp_model.decision.input.processed.example;

import ao.ai.opp_model.decision.input.processed.attribute.Attribute;
import ao.ai.opp_model.decision.input.processed.data.LocalDatum;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public class LocalExampleImpl
        implements LocalExample
{
    //--------------------------------------------------------------------
    private final LocalDatum TARGET;
    private final LocalContext CONTEXT;


    //--------------------------------------------------------------------
    public LocalExampleImpl(LocalContext context, LocalDatum target)
    {
        CONTEXT = context;
        TARGET  = target;
    }


    //--------------------------------------------------------------------
    protected LocalContext contextDeleget()
    {
        return CONTEXT;
    }


    //--------------------------------------------------------------------
    public LocalDatum target()
    {
        return TARGET;
    }

    public Attribute targetAttribute()
    {
        return TARGET.attribute();
    }

    //--------------------------------------------------------------------
    public LocalExample withTarget(LocalDatum target)
    {
        return CONTEXT.withTarget(target);
    }

    public List<Attribute> dataAttributes()
    {
        return CONTEXT.dataAttributes();
    }

    public Collection<LocalDatum> data()
    {
        return CONTEXT.data();
    }

    public void add(LocalDatum datum)
    {
        CONTEXT.add( datum );
    }

    public void addAll(LocalContext dataFrom)
    {
        CONTEXT.addAll(dataFrom);
    }

    public LocalDatum datumOfType(Attribute attribute)
    {
        return CONTEXT.datumOfType( attribute );
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return CONTEXT.toString() + "\t->\t" + TARGET.toString();
    }
}
