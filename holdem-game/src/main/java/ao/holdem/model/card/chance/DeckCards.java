package ao.holdem.model.card.chance;

import ao.holdem.model.Avatar;
import ao.holdem.model.Round;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DeckCards implements ChanceCards
{
    //--------------------------------------------------------------------
    private final Deck       cards;
    private final List<Hole> holes;
    private       Community  community = Community.PREFLOP;


    //--------------------------------------------------------------------
    public DeckCards()
    {
        cards = new Deck();
        holes = new ArrayList<>();
    }


    //--------------------------------------------------------------------
    @Override
    public Hole hole(int forPlayer)
    {
        while (holes.size() <= forPlayer) {
            holes.add(null);
        }

        Hole hole = holes.get( forPlayer );
        if (hole == null)
        {
            hole = cards.nextHole();
            holes.set(forPlayer, hole);
        }
        return hole;
    }


    //--------------------------------------------------------------------
    public Community community(Round asOf)
    {
//        Round safeRound =
//                (asOf == null)
//                 ? Round.RIVER : asOf;
        switch (asOf)
        {
            case RIVER:
                flop();
                turn();
                river();
                break;
            
            case TURN:
                flop();
                turn();
                break;

            case FLOP:
                flop();
                break;
        }

//        return community.asOf( safeRound );
        return community.asOf( asOf );
    }

    private void flop()
    {
        if (! community.hasFlop())
        {
            community = cards.nextFlop();
        }
    }
    private void turn()
    {
        if (! community.hasTurn())
        {
            community = community.addTurn( cards.nextCard() );
        }
    }
    private void river()
    {
        if (! community.hasRiver())
        {
            community = community.addRiver( cards.nextCard() );
        }
    }
}
