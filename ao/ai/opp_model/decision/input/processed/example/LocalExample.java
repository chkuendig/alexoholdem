package ao.ai.opp_model.decision.input.processed.example;

import ao.ai.opp_model.decision.input.processed.data.LocalDatum;
import ao.ai.opp_model.decision.input.processed.attribute.Attribute;

/**
 *
 */
public interface LocalExample extends LocalContext
{
    LocalDatum target();

    Attribute targetAttribute();
}
