package ao.ai.simple;

import ao.ai.AbstractPlayer;
import ao.holdem.engine.analysis.Analysis;
import ao.holdem.engine.state.State;
import ao.holdem.model.Avatar;
import ao.holdem.model.Chips;
import ao.holdem.model.act.Action;
import ao.holdem.model.act.FallbackAction;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;

import java.util.Map;

/**
 * Strategy from
 *  http://www.pokertips.org/strategy/longhand.php
 */
public class DuaneBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    public void handEnded(Map<Avatar, Chips> deltas) {}

    
    //--------------------------------------------------------------------
    public Action act(State     state,
                      Hole      hole,
                      Community community,
                      Analysis  analysis)
    {
        int group = Sklansky.groupOf( hole );

        FallbackAction act =
                (group <= 4)
                 ? FallbackAction.RAISE_OR_CALL
                 : FallbackAction.CHECK_OR_FOLD;
        return state.reify( act );
    }
}
