package ao.ai.simple;

import ao.ai.AbstractPlayer;
import ao.state.HandState;
import ao.holdem.model.Hole;
import ao.holdem.model.act.EasyAction;
import ao.state.StateManager;
import ao.strategy.Sklansky;

/**
 * Strategy from
 *  http://www.pokertips.org/strategy/longhand.php
 */
public class DuaneBot extends AbstractPlayer
{
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
