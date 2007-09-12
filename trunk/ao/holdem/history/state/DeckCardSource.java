package ao.holdem.history.state;

import ao.holdem.def.model.cards.Community;
import ao.holdem.def.model.cards.Deck;
import ao.holdem.def.model.cards.Hole;
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
    private       Community               flop, turn, river;


    //--------------------------------------------------------------------
    public DeckCardSource()
    {
        cards = new Deck();
        holes = new HashMap<Serializable, Hole>();
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

    public Community flop()
    {
        if (flop == null)
        {
            flop = cards.nextFlop();
        }
        return flop;
    }

    public Community turn()
    {
        if (turn == null)
        {
            turn = flop().addTurn(cards.nextCard());
        }
        return turn;
    }

    public Community river()
    {
        if (river == null)
        {
            river = turn().addTurn(cards.nextCard());
        }
        return river;
    }
}
