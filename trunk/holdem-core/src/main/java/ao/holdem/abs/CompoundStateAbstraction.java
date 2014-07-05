package ao.holdem.abs;

import ao.holdem.abs.act.ActionAbstraction;
import ao.holdem.abs.card.CardAbstraction;
import ao.holdem.engine.eval.odds.OddsEvaluator;
import ao.holdem.engine.state.ActionState;
import ao.holdem.model.Round;
import ao.holdem.model.card.sequence.CardSequence;

public class CompoundStateAbstraction implements StateAbstraction
{
    private final CardAbstraction cardAbstraction;
    private final ActionAbstraction actionAbstraction;
    private final OddsEvaluator oddsEvaluator;


    public CompoundStateAbstraction(
            CardAbstraction cardAbstraction,
            ActionAbstraction actionAbstraction,
            OddsEvaluator oddsEvaluator)
    {
        this.cardAbstraction = cardAbstraction;
        this.actionAbstraction = actionAbstraction;
        this.oddsEvaluator = oddsEvaluator;
    }


    @Override
    public int indexOf(ActionState actionState, CardSequence cardState) {
        Round round = actionState.round();

        int actionInRound = actionAbstraction.indexInRound(actionState);
        int cardInRound = cardAbstraction.indexInRound(cardState, oddsEvaluator);
        int cardsInRound = cardAbstraction.count(round);

        int roundOffset = offset(actionState.round());
        int indexInRound = actionInRound * cardsInRound + cardInRound;

        return roundOffset + indexInRound;
    }

    @Override
    public int size() {
        int sum = 0;
        for (Round round : Round.VALUES) {
            sum += count(round);
        }
        return sum;
    }


    private int count(Round round) {
        return actionAbstraction.count(round) * cardAbstraction.count(round);
    }

    private int offset(Round round) {
        int offset = 0;

        Round cursor = round;
        while (cursor != Round.PREFLOP) {
            cursor = cursor.previous();
            offset += count(cursor);
        }

        return offset;
    }
}
