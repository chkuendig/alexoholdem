package ao.holdem.history;

import ao.holdem.def.state.domain.BettingRound;

import java.util.List;

/**
 *
 */
public class HandHistory
{
    //--------------------------------------------------------------------
    //in order of cards received
    public void addPlayer(PlayerHandle player)
    {

    }

    public void addWinner(PlayerHandle smallBlind)
    {

    }

    public void addEvent(PlayerHandle smallBlind)
    {

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
