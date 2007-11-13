package ao.ai.opp_model.decision.example;

import ao.ai.opp_model.decision.data.Datum;
import ao.ai.opp_model.decision.attribute.Attribute;

/**
 *
 */
public interface Example extends Context
{
    Datum target();

    Attribute targetAttribute();
}
