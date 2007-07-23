package ao.holdem.def.history_bot;

import ao.holdem.def.state.action.Action;
import ao.holdem.history.HandHistory;
import ao.holdem.history.Snapshot;

/**
 *
 */
public interface HistoryBot
{
    public Action act(
            HandHistory hand,
            Snapshot env);
}
