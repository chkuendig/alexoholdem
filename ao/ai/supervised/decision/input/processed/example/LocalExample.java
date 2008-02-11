package ao.ai.supervised.decision.input.processed.example;

import ao.ai.supervised.decision.input.processed.data.LocalDatum;
import ao.ai.supervised.decision.input.processed.attribute.Attribute;

/**
 *
 */
public interface LocalExample extends LocalContext
{
    LocalDatum target();

    Attribute targetAttribute();
}
