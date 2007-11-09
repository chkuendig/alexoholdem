package ao.ai.opp_model.decision2.example;

import ao.ai.opp_model.decision2.attribute.Attribute;
import ao.ai.opp_model.decision2.data.Datum;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public class ExampleImpl
        implements Example
{
    //--------------------------------------------------------------------
    private final Datum   TARGET;
    private final Context CONTEXT;


    //--------------------------------------------------------------------
    public ExampleImpl(Context context, Datum target)
    {
        CONTEXT = context;
        TARGET  = target;
    }


    //--------------------------------------------------------------------
    protected Context contextDeleget()
    {
        return CONTEXT;
    }


    //--------------------------------------------------------------------
    public Datum target()
    {
        return TARGET;
    }

    public Attribute targetAttribute()
    {
        return TARGET.attribute();
    }

    //--------------------------------------------------------------------
    public Example withTarget(Datum target)
    {
        return CONTEXT.withTarget(target);
    }

    public List<Attribute> dataAttributes()
    {
        return CONTEXT.dataAttributes();
    }

    public Collection<Datum> data()
    {
        return CONTEXT.data();
    }

    public void add(Datum datum)
    {
        CONTEXT.add( datum );
    }

    public Datum datumOfType(Attribute attribute)
    {
        return CONTEXT.datumOfType( attribute );
    }
}
