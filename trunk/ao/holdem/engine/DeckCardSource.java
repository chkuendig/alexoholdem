package ao.holdem.engine;

import ao.holdem.model.card.Community;
import ao.holdem.model.card.Deck;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.CardSource;
import ao.holdem.engine.persist.PlayerHandle;

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
    private       Community               community = new Community();


    //--------------------------------------------------------------------
    public DeckCardSource()
    {
        cards = new Deck();
        holes = new HashMap<Serializable, Hole>();
    }

    private DeckCardSource(Deck copyCards,
                           Map<Serializable, Hole> copyHoles,
                           Community copyCommunity)
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
        if (! community.hasFlop())
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
