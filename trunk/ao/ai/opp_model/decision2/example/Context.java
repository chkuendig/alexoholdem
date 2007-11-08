package ao.ai.opp_model.decision2.example;

import ao.ai.opp_model.decision2.attribute.Attribute;
import ao.ai.opp_model.decision2.data.Datum;

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
