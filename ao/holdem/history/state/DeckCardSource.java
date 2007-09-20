package ao.holdem.history.state;

import ao.holdem.model.Community;
import ao.holdem.model.Deck;
import ao.holdem.model.Hole;
import ao.holdem.history.PlayerHandle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DeckCardSource implements CardSource
{
    //--------------------------------------------------------------------
    private final Deck                    cards;
    private final Map<Serializable, Hole> holes;
    private       Community               community;


    //--------------------------------------------------------------------
    public DeckCardSource()
    {
        cards = new Deck();
        holes = new HashMap<Serializable, Hole>();
    }

    private DeckCardSource(Deck copyCards,
                           Map<Serializable, Hole> copyHoles,
                           Community               copyCommunity)
    {
        cards     = copyCards;
        holes     = copyHoles;
        community = copyCommunity;
    }


    //--------------------------------------------------------------------
    public Hole holeFor(PlayerHandle player)
    {
        Hole hole = holes.get( player.getId() );
        if (hole == null)
        {
            hole = cards.nextHole();
            holes.put( player.getId(), hole );
        }
        return hole;
    }


    //--------------------------------------------------------------------
    public Community community()
    {
        return community;
    }

    public void flop()
    {
        if (community == null)
        {
            community = cards.nextFlop();
        }
    }

    public void turn()
    {
        if (! community.hasTurn())
        {
            community = community.addTurn( cards.nextCard() );
        }
    }

    public void river()
    {
        if (! community.hasRiver())
        {
            community = community.addRiver(cards.nextCard());
        }
    }


    //--------------------------------------------------------------------
    public CardSource prototype()
    {
        return new DeckCardSource(cards.prototype(),
                                  new HashMap<Serializable, Hole>(holes),
                                  community);
    }
}
