package ao.holdem.def.history_bot;

import ao.holdem.def.model.Money;
import ao.holdem.def.state.action.Action;
import ao.holdem.history.HandHistory;
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
    public Action act(
            HandHistory hand,
            Snapshot env);


    //--------------------------------------------------------------------
    /**
     * at the end of each hand, this method is called.
     * @param stackDelta money lost/won during last hand.
     */
    public void tally(Money stackDelta);
}
