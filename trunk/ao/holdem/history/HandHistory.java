package ao.holdem.history;

import ao.holdem.def.state.domain.BettingRound;

import java.util.List;
import java.util.ArrayList;

/**
 *
 */
public class HandHistory
{
    //--------------------------------------------------------------------
    private List<PlayerHandle> players = new ArrayList<PlayerHandle>();
    private List<PlayerHandle> winners = new ArrayList<PlayerHandle>();

    private List<Event> preflop = new ArrayList<Event>();
    private List<Event> flop    = new ArrayList<Event>();
    private List<Event> turn    = new ArrayList<Event>();
    private List<Event> river   = new ArrayList<Event>();


    //--------------------------------------------------------------------
    //in order of cards received
    public void addPlayer(PlayerHandle player)
    {
        players.add( player );
    }

    public void addWinner(PlayerHandle winner)
    {
        winners.add( winner );
    }

    public void addEvent(BettingRound round, Event event)
    {
        ((round == BettingRound.PREFLOP)
          ? preflop
          : (round == BettingRound.FLOP)
             ? flop
             : (round == BettingRound.TURN)
                ? turn
                : river
        ).add( event );
    }


    //--------------------------------------------------------------------
    //in order of cards received
    public List<PlayerHandle> players()
    {
        return null;
    }

    public List<Event> actionDuring(
            BettingRound round)
    {
        return null;
    }

    public List<PlayerHandle> winners()
    {
        return null;
    }
}
