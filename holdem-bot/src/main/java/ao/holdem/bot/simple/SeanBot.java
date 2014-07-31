package ao.holdem.bot.simple;

import ao.holdem.bot.AbstractPlayer;
import ao.holdem.bot.simple.starting_hands.Sklansky;
import ao.holdem.engine.state.ActionState;
import ao.holdem.model.Round;
import ao.holdem.model.act.Action;
import ao.holdem.model.act.FallbackAction;
import ao.holdem.model.card.sequence.CardSequence;

public class SeanBot extends AbstractPlayer {

    @Override
    public Action act(ActionState state, CardSequence cards) {
        int sklansky = Sklansky.groupOf(cards.hole());

        Action move = state.reify(FallbackAction.CHECK_OR_FOLD);

        if (sklansky <= 7) {
            move = state.reify(FallbackAction.RAISE_OR_CALL);
        } else if (sklansky <= 8 || state.round() == Round.PREFLOP) {
            move = state.reify(FallbackAction.CHECK_OR_CALL);
        }

        return move;
    }
}
