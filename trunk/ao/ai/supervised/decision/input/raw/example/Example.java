package ao.ai.supervised.decision.input.raw.example;

import ao.ai.supervised.decision.input.processed.data.DataPool;
import ao.ai.supervised.decision.input.processed.example.LocalExample;

/**
 * External Example
 */
public interface Example extends Context
{
    public Datum target();

    public String targetType();

    public LocalExample toExample(DataPool pool);
}
