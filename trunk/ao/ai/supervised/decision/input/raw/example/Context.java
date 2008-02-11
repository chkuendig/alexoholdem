package ao.ai.supervised.decision.input.raw.example;

import ao.ai.supervised.decision.input.processed.data.DataPool;
import ao.ai.supervised.decision.input.processed.example.LocalContext;

import java.util.Collection;
import java.util.List;

/**
 * External Context
 */
public interface Context
{
    public Example withTarget(Datum target);

    public void add(Datum datum);
    public void addAll(Context dataFrom);

    public Collection<String> types();
    public List<Datum>        data();

    public LocalContext toContext(DataPool pool);
}
