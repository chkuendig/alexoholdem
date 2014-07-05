package ao.holdem.engine.eval.odds;

import ao.holdem.model.card.sequence.CardSequence;

public interface OddsEvaluator
{
    double approximateHeadsUpHandStrength(CardSequence cards);
}
