package ao.ai.simple;

import ao.ai.AbstractPlayer;
import ao.holdem.engine.analysis.Analysis;
import ao.holdem.engine.state.State;
import ao.holdem.v3.model.Avatar;
import ao.holdem.v3.model.Chips;
import ao.holdem.v3.model.act.Action;
import ao.holdem.v3.model.act.FallbackAction;
import ao.holdem.v3.model.card.Community;
import ao.holdem.v3.model.card.Hole;

import java.util.Map;

/**
 *
 */
public class AlwaysRaiseBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    public void handEnded(Map<Avatar, Chips> deltas) {}

    
    //--------------------------------------------------------------------
    public Action act(State     state,
                      Hole      hole,
                      Community community,
                      Analysis  analysis)
    {
        return state.reify(FallbackAction.RAISE_OR_CALL);
    }
}
