package ao.ai.simple;

import ao.strategy.Sklansky;
import ao.holdem.def.bot.AbstractBot;
import ao.holdem.model.Hole;
import ao.holdem.def.state.action.Action;
import ao.holdem.def.state.env.Environment;

/**
 * Strategy from
 *  http://www.pokertips.org/strategy/longhand.php
 */
public class DuaneBot extends AbstractBot
{
    //--------------------------------------------------------------------
    public Action act(Environment env)
    {
        Hole hole = env.hole();
        int group = Sklansky.groupOf( hole );

        if (group <= 4)
        {
            return Action.RAISE_OR_CALL;
        }
        return Action.CHECK_OR_FOLD;
    }
}
