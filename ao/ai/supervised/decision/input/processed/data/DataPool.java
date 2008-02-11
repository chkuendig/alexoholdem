package ao.ai.supervised.decision.input.processed.data;

import ao.ai.supervised.decision.input.processed.attribute.Attribute;
import ao.ai.supervised.decision.input.processed.attribute.Continuous;
import ao.ai.supervised.decision.input.processed.attribute.Multistate;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DataPool
{
    //--------------------------------------------------------------------
    private Map<String, Attribute> attributes;


    //--------------------------------------------------------------------
    public DataPool()
    {
        attributes = new HashMap<String, Attribute>();
    }


    //--------------------------------------------------------------------
    public <T extends Enum<T>>
            LocalDatum fromEnum(T enumValue)
    {
        return newMultistate(
                enumValue.getDeclaringClass().getSimpleName(),
                enumValue);
    }

    public LocalDatum fromTyped(Object state)
    {
        return newMultistate(state.getClass().getSimpleName(),
                             state);
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
