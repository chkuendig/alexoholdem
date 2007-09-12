package ao.holdem.bots.hist;

import ao.holdem.bots.util.Util;
import ao.holdem.def.history_bot.HistoryBot;
import ao.holdem.def.model.Money;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.state.action.Action;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.HandHistory;
import ao.holdem.history.PlayerHandle;
import ao.holdem.history.Snapshot;

/**
 *
 */
public class BlindBot implements HistoryBot
{
    public void introduce() {}
    public void retire()    {}

    public void opponentToAct(HandHistory hand, Snapshot env) {}

    public void opponentActed(
            PlayerHandle opponent,
            HandHistory hand, Snapshot env, TakenAction act)  {}

    public void handStarted() {}
    public void handEnded(
            HandHistory atEndOfHand, Money stackDelta) {}

    public Action act(HandHistory hand, Snapshot env)
    {
        Hole hole = hand.getHoles().get( env.nextToAct() );
        int group = Util.sklanskyGroup( hole );

        if (group <= 4)
        {
            return Action.RAISE_OR_CALL;
        }
        return Action.CHECK_OR_FOLD;
    }
}
