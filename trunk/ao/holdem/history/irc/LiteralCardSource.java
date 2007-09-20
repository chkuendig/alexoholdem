package ao.holdem.history.irc;

import ao.holdem.model.Community;
import ao.holdem.model.Hole;
import ao.holdem.history.PlayerHandle;
import ao.holdem.history.state.CardSource;

import java.util.Map;
import java.util.HashMap;

/**
 *
 */
public class LiteralCardSource implements CardSource
{
    //--------------------------------------------------------------------
    private Map<PlayerHandle, Hole> holes;
    private Community               community;


    //--------------------------------------------------------------------
    public LiteralCardSource()
    {
        holes = new HashMap<PlayerHandle, Hole>();
    }


    //--------------------------------------------------------------------
    public void setCommunity(Community community)
    {
        this.community = community;
    }

    public void putHole(PlayerHandle player, Hole hole)
    {
        holes.put(player, hole);
    }


    //--------------------------------------------------------------------
    public Hole holeFor(PlayerHandle player)
    {
        return holes.get(player);
    }

    public Community community()
    {
        return community;
    }


    //--------------------------------------------------------------------
    public void flop()  {}
    public void turn()  {}
    public void river() {}


    //--------------------------------------------------------------------
    public CardSource prototype()
    {
        return this;
    }
}
