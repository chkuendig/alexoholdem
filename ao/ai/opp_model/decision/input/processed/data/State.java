package ao.ai.opp_model.decision.input.processed.data;

import ao.ai.opp_model.decision.input.processed.attribute.Attribute;

/**
 *
 */
public class State extends LocalDatum
{
    //--------------------------------------------------------------------
    private final Object STATE;


    //--------------------------------------------------------------------
    public State(Attribute attribute, Object state)
    {
        super(attribute);
        STATE = state;
    }


    //--------------------------------------------------------------------
    public boolean contains(LocalDatum datum)
    {
        return datum instanceof State &&
               STATE.equals(((State) datum).STATE);
    }


    //--------------------------------------------------------------------
    public Object state()
    {
        return STATE;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return String.valueOf(STATE);
    }


    //--------------------------------------------------------------------
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;
        return STATE.equals(state.STATE);
    }

    public int hashCode()
    {
        return STATE.hashCode();
    }
}
