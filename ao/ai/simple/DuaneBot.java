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
