package ao.holdem.def.history_bot;

import ao.holdem.model.Money;
import ao.holdem.model.act.Action;
import ao.holdem.model.act.SimpleAction;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;

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
            SimpleAction act);


    //--------------------------------------------------------------------
    public Action act(
            HandHistory hand,
            Snapshot env);


    //--------------------------------------------------------------------
    public void handStarted();

    /**
     * At the end of each hand, this method is called.
     * @param stackDelta money lost/won during last hand.
     * @param atEndOfHand includes all action during the hand.
     */
    public void handEnded(HandHistory atEndOfHand, Money stackDelta);
}
