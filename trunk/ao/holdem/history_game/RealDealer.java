package ao.holdem.history_game;

import ao.holdem.def.state.env.RealAction;
import ao.holdem.history.PlayerHandle;
import ao.holdem.history.state.CumulativeState;
import ao.holdem.history.state.PlayerState;
import ao.holdem.history.state.StatePlayer;

import java.util.Map;

/**
 *
 */
public class RealDealer
{
    //--------------------------------------------------------------------
    private final CumulativeState                start;
    private final Map<PlayerHandle, StatePlayer> players;


    //--------------------------------------------------------------------
    public RealDealer(CumulativeState                startFrom,
                      Map<PlayerHandle, StatePlayer> brains)
    {
        start   = startFrom;
        players = brains;
    }


    //--------------------------------------------------------------------
    public CumulativeState playOutHand()
    {
        CumulativeState state = start.prototype();
        do
        {
            PlayerState player = state.head().nextToAct();
            RealAction act = players.get( player.handle() ).act( state );
            state.advance( act );
        }
        while ( !state.head().atEndOfHand() );
        return state;
    }
}
