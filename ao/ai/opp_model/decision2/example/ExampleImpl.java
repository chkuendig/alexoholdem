package ao.ai.opp_model.decision2.example;

import ao.ai.opp_model.decision2.data.Datum;
import ao.ai.opp_model.decision2.attribute.Attribute;

import java.util.Collection;

/**
 *
 */
public class ExampleImpl
        implements Context, Example
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

    //--------------------------------------------------------------------
    public Example withTarget(Datum target)
    {
        return CONTEXT.withTarget(target);
    }

    public Collection<Attribute> attributes()
    {
        return CONTEXT.attributes();
    }

    public Collection<Datum> attributeData()
    {
        return CONTEXT.attributeData();
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
