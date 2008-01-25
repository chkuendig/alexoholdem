package ao.ai.simple;

import ao.ai.AbstractPlayer;
import ao.holdem.model.act.EasyAction;
import ao.holdem.model.card.Hole;
import ao.persist.HandHistory;
import ao.state.HandState;
import ao.state.StateManager;
import ao.ai.simple.Sklansky;

/**
 * Strategy from
 *  http://www.pokertips.org/strategy/longhand.php
 */
public class DuaneBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    public void handEnded(HandHistory history) {}

    
    //--------------------------------------------------------------------
    protected EasyAction act(StateManager env, HandState state, Hole hole)
    {
        int group = Sklansky.groupOf( hole );

        if (group <= 4)
        {
            return EasyAction.RAISE_OR_CALL;
        }
        return EasyAction.CHECK_OR_FOLD;
    }
}
