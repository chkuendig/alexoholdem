package ao.ai.opp_model.decision2.example;

import ao.ai.opp_model.decision2.data.Datum;
import ao.ai.opp_model.decision2.attribute.Attribute;

/**
 *
 */
public interface Example extends Context
{
    Datum target();

    Attribute targetAttribute();
}
