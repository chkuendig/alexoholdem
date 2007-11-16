package ao.ai.opp_model.decision.attribute;

import ao.ai.opp_model.decision.data.Datum;
import ao.ai.opp_model.decision.data.State;

import java.util.*;

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

    public Collection<? extends Datum> orderedPartition()
    {
        Object aState = byDatum.values().iterator().next().state();
        if (! (aState instanceof Comparable)) return partition();

        Collection<State> parts =
                new TreeSet<State>(new Comparator<State>() {
                    @SuppressWarnings("unchecked")
                    public int compare(State a, State b) {
                        return ((Comparable) a.state())
                                    .compareTo( b.state() );
                    }
                });
        parts.addAll( byDatum.values() );
        return parts;
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
