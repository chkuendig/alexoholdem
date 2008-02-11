package ao.ai.supervised.decision.input.raw.example;

import ao.ai.supervised.decision.input.processed.data.DataPool;
import ao.ai.supervised.decision.input.processed.example.LocalContext;
import ao.ai.supervised.decision.input.processed.example.LocalExample;
import ao.ai.supervised.decision.input.processed.example.LocalExampleImpl;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public class ExampleImpl implements Example
{
    //--------------------------------------------------------------------
    private final Context DELEGET;
    private final Datum TARGET;


    //--------------------------------------------------------------------
    public ExampleImpl(Context context, Datum target)
    {
        DELEGET = context;
        TARGET  = target;
    }


    //--------------------------------------------------------------------
    public Datum target()
    {
        return TARGET;
    }

    public String targetType()
    {
        return TARGET.type();
    }


    //--------------------------------------------------------------------
    public LocalExample toExample(DataPool pool)
    {
        return new LocalExampleImpl(DELEGET.toContext(pool),
                                    TARGET.toDatum(pool));
    }

    public LocalContext toContext(DataPool pool)
    {
        return DELEGET.toContext(pool);
    }


    //--------------------------------------------------------------------
    public Example withTarget(Datum target)
    {
        return DELEGET.withTarget( target );
    }

    public void add(Datum datum)
    {
        DELEGET.add(datum);
    }

    public void addAll(Context dataFrom)
    {
        DELEGET.addAll(dataFrom);
    }

    public Collection<String> types()
    {
        return DELEGET.types();
    }

    public List<Datum> data()
    {
        return DELEGET.data();
    }
}
