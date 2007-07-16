package ao.holdem.def.bot;

import ao.holdem.def.state.action.Action;
import ao.holdem.history.Spanshot;

/**
 * Texus Hold'em limit game bot.
 */
public interface Bot
{
    //--------------------------------------------------------------------
    public void introduce();
    public void retire();


    //--------------------------------------------------------------------
    public Action act(Spanshot env);


    //--------------------------------------------------------------------
    // these two methods must be properly defined.
    @Override public int hashCode();
    @Override public boolean equals(Object o);
}
