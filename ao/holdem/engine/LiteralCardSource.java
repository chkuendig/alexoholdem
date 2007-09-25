package ao.holdem.engine;

import ao.holdem.model.BettingRound;
import ao.holdem.model.CardSource;
import ao.holdem.model.Community;
import ao.holdem.model.Hole;
import ao.persist.PlayerHandle;

import java.util.HashMap;
import java.util.Map;

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
    public Community community(BettingRound asOf)
    {
        return community.asOf( asOf );
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
