package ao.holdem.bots;

import ao.holdem.def.bot.Bot;
import ao.holdem.def.state.action.Action;
import ao.holdem.def.state.env.Environment;
import ao.util.rand.Rand;


/**
 *
 */
public class RandomBot implements Bot
{
    public Action act(Environment env)
    {
        return Rand.fromArray( Action.values() );
    }

    @Override
    public String toString()
    {
        return "RandomBot";
    }
}
