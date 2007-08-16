package ao.holdem.def.history_bot;

import ao.holdem.def.model.Money;
import ao.holdem.def.state.action.Action;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.HandHistory;
import ao.holdem.history.PlayerHandle;
import ao.holdem.history.Snapshot;

/**
 *
 */
public interface HistoryBot
{
    //--------------------------------------------------------------------
    public void introduce();
    public void retire();


    //--------------------------------------------------------------------
    /**
     * Starts after the big blind, where the HandHistory is fresh.
     * Fires even after you fold.
     *
     * @param hand as opponent saw it.
     * @param env  last Snapshot of HandHistory.
     */
    public void opponentToAct(
            HandHistory hand,
            Snapshot    env);

    /**
     * Fires even after you fold.
     *
     * @param hand after opponent's action.
     * @param env last Snapshot of HandHistory.
     * @param act action taken by opponent (in included in HandHistory).
     * @param opponent actor.
     */
    public void opponentActed(
            PlayerHandle opponent,
            HandHistory  hand,
            Snapshot     env,
            TakenAction  act);


    //--------------------------------------------------------------------
    public Action act(
            HandHistory hand,
            Snapshot    env);


    //--------------------------------------------------------------------
    public void handStarted();

    /**
     * At the end of each hand, this method is called.
     * @param stackDelta money lost/won during last hand.
     * @param atEndOfHand includes all action during the hand.
     */
    public void handEnded(HandHistory atEndOfHand, Money stackDelta);
}
