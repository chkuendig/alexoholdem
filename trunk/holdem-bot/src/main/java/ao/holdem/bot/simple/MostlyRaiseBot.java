package ao.holdem.bot.simple;

import ao.holdem.bot.AbstractPlayer;
import ao.holdem.engine.state.State;
import ao.holdem.model.Avatar;
import ao.holdem.model.ChipStack;
import ao.holdem.model.act.Action;
import ao.holdem.model.act.FallbackAction;
import ao.holdem.model.card.sequence.CardSequence;

import java.util.Map;

/**
 * 22/02/14 8:27 PM
 */
public class MostlyRaiseBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    public void handEnded(Map<Avatar, ChipStack> deltas) {}


    //--------------------------------------------------------------------
    public Action act(State state, CardSequence cards)
    {
        boolean shouldRaise = Math.random() < 0.66;

        return state.reify(
                shouldRaise
                ? FallbackAction.RAISE_OR_CALL
                : FallbackAction.CHECK_OR_CALL);
    }
}
