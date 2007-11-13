package ao.ai.opp_model.decision.example;

import ao.ai.opp_model.decision.attribute.Attribute;
import ao.ai.opp_model.decision.data.Datum;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public interface Context
{
    public Example withTarget(Datum target);

    public List<Attribute> dataAttributes();

    public Collection<Datum> data();

    public void add(Datum datum);

    public Datum datumOfType(Attribute attribute);
}
