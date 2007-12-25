package ao.ai.opp_model.decision.input.raw.example;

import ao.ai.opp_model.decision.input.processed.data.DataPool;
import ao.ai.opp_model.decision.input.processed.example.LocalExample;

/**
 * External Example
 */
public interface Example extends Context
{
    public Datum target();

    public String targetType();

    public LocalExample toExample(DataPool pool);
}
