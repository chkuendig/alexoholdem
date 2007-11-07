package ao.ai.opp_model.decision2.example;

import ao.ai.opp_model.decision2.data.Datum;
import ao.ai.opp_model.decision2.attribute.Attribute;

import java.util.Collection;

/**
 *
 */
public interface Context
{
    public Example withTarget(Datum target);

    public Collection<Attribute> attributes();

    public Collection<Datum> attributeData();

    public void add(Datum datum);

    public Datum datumOfType(Attribute attribute);
}
