package ao.holdem.abs.card;

import ao.holdem.engine.eval.odds.OddsEvaluator;
import ao.holdem.model.Round;
import ao.holdem.model.card.sequence.CardSequence;

/**
 *
 */
public interface CardAbstraction
{
    int indexInRound(CardSequence cards, OddsEvaluator oddsEvaluator);

    int count(Round round);
}
