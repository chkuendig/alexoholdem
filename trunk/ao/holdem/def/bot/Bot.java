package ao.holdem.def.bot;

import ao.holdem.def.state.action.Action;
import ao.holdem.def.state.env.Environment;

/**
 * Texus Hold'em limit game bot.
 */
public interface Bot
{
    public Action act(Environment env);
}
