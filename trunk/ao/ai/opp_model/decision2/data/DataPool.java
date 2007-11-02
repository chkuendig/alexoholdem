package ao.ai.opp_model.decision2.data;

import ao.ai.opp_model.decision2.attribute.Attribute;
import ao.ai.opp_model.decision2.attribute.Continuous;
import ao.ai.opp_model.decision2.attribute.Multistate;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DataPool
{
    //--------------------------------------------------------------------
    private Map<Object, Attribute> attributes;


    //--------------------------------------------------------------------
    public DataPool()
    {
        attributes = new HashMap<Object, Attribute>();
    }


    //--------------------------------------------------------------------
    public <T extends Enum<T>>
            Datum fromEnum(T enumValue)
    {
        return newMultistate(
                enumValue.getDeclaringClass().getSimpleName(),
                enumValue);
    }

    public Datum fromTyped(Object value)
    {
        return newMultistate(value.getClass().getSimpleName(),
                             value);
    }


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public State newMultistate(String type, Object state)
    {
        Multistate multistate = (Multistate) attributes.get(type);
        if (multistate == null)
        {
            multistate = new Multistate(type);
            attributes.put(type, multistate);
        }
        return multistate.addIfAbsent( state );
    }


    //--------------------------------------------------------------------
    public Value newContinuous(String type, double value)
    {
        Continuous continuous = (Continuous) attributes.get(type);
        if (continuous == null)
        {
            continuous = new Continuous(type);
            attributes.put(type, continuous);
        }
        return continuous.add( value );
    }
}
