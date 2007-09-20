package ao.ai.simple;

import ao.holdem.def.bot.AbstractBot;
import ao.holdem.def.state.action.Action;
import ao.holdem.def.state.env.Environment;
import ao.util.rand.Rand;


/**
 *
 */
public class RandomBot extends AbstractBot
{
    //--------------------------------------------------------------------
    public Action act(Environment env)
    {
        return Rand.fromArray( Action.values() );
    }


    //--------------------------------------------------------------------
//    @Override
//    public String toString()
//    {
//        return "RandomBot";
//    }
}
