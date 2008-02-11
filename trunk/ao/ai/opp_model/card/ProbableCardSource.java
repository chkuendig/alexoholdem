package ao.ai.opp_model.card;

import ao.holdem.engine.persist.PlayerHandle;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.CardSource;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class ProbableCardSource implements CardSource
{
    //--------------------------------------------------------------------
    private Community               community;
    private Map<PlayerHandle, Hole> holes;


    //--------------------------------------------------------------------
    public ProbableCardSource(
            List<Card>              allCommunity,
            Map<PlayerHandle, Hole> exclusiveHoles)
    {
        holes     = exclusiveHoles;
        community = new Community(allCommunity.get(0),
                                  allCommunity.get(1),
                                  allCommunity.get(2),
                                  allCommunity.get(3),
                                  allCommunity.get(4));
    }


    //--------------------------------------------------------------------
    public Community community()
    {
        return community;
    }

    public void flop()  {}
    public void turn()  {}
    public void river() {}


    //--------------------------------------------------------------------
    public Hole holeFor(PlayerHandle player)
    {
        return holes.get( player );
    }


    //--------------------------------------------------------------------
    public CardSource prototype()
    {
        return this;
    }
}
