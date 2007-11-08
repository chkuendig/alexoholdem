package ao.ai.opp_model.decision2.attribute;

import ao.ai.opp_model.decision2.data.Datum;
import ao.ai.opp_model.decision2.data.State;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

/**
 *
 */
public class Multistate extends TypedAttribute
{
    //--------------------------------------------------------------------
    private Map<Object, State> byDatum;


    //--------------------------------------------------------------------
    public Multistate(String type)
    {
        super(type);
        byDatum = new HashMap<Object, State>();
    }


    //--------------------------------------------------------------------
    public State addIfAbsent(Object datum)
    {
        State state = byDatum.get( datum );
        if (state == null)
        {
            state = new State(this, datum);
            byDatum.put(datum, state);
        }
        return state;
    }


    //--------------------------------------------------------------------
    public boolean isSingleUse()
    {
        return true;
    }


    //--------------------------------------------------------------------
    public Collection<? extends Datum> partition()
    {
        return byDatum.values();
    }


    //--------------------------------------------------------------------
    public Collection<? extends Attribute> views()
    {
        return Collections.singleton(this);
    }

    public Attribute randomView()
    {
        return this;
    }

    public double viewChoiceLength()
    {
        return 0;
    }
}
