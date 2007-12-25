package ao.ai.opp_model.decision.input.raw.example;

import ao.ai.opp_model.decision.input.processed.data.DataPool;
import ao.ai.opp_model.decision.input.processed.data.LocalDatum;
import org.jetbrains.annotations.NotNull;

/**
 * External Datum
 */
public class Datum
{
    //--------------------------------------------------------------------
    private final String type;
    private final Object state;
    private final double value;


    //--------------------------------------------------------------------
    public <T extends Enum<T>> Datum(@NotNull T enumValue)
    {
        this(enumValue.getDeclaringClass().getSimpleName(),
             enumValue);
    }

    public Datum(@NotNull Object state)
    {
        this(state.getClass().getSimpleName(), state, 0);
    }

    public Datum(@NotNull String type,
                 @NotNull Object state)
    {
        this(type, state, 0);
    }

    public Datum(@NotNull String type,
                          double value)
    {
        this(type, null, value);
    }

    private Datum(String type, Object state, double value)
    {
        this.type  = type;
        this.state = state;
        this.value = value;
    }

    
    //--------------------------------------------------------------------
    public String type()
    {
        return type;
    }


    //--------------------------------------------------------------------
    public LocalDatum toDatum(DataPool inPool)
    {
        return isMultistate()
                ? inPool.newMultistate(type, state)
                : inPool.newContinuous(type, value);
    }

    private boolean isMultistate()
    {
        return (state != null);
    }
}
