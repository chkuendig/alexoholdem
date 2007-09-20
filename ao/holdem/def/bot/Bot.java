package ao.holdem.def.bot;

import ao.holdem.model.act.Action;

/**
 * Texus Hold'em limit game bot.
 */
public interface Bot
{
    //--------------------------------------------------------------------
    public void introduce();
    public void retire();


    //--------------------------------------------------------------------
//    public Action act(Snapshot env);
    public Action act(Environment env);


    //--------------------------------------------------------------------
    // these two methods must be properly defined.
    @Override public int hashCode();
    @Override public boolean equals(Object o);
}
