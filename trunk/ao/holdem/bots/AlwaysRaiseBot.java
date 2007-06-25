package ao.holdem.bots;

import ao.holdem.def.bot.AbstractBot;
import ao.holdem.def.state.action.Action;
import ao.holdem.def.state.env.Environment;

/**
 *
 */
public class AlwaysRaiseBot extends AbstractBot
{
    //--------------------------------------------------------------------
    public Action act(Environment env)
    {
        return Action.RAISE_OR_CALL;
    }


    //--------------------------------------------------------------------
//    public String toString()
//    {
//        return "AlwaysRaiseBot";
//    }
}
