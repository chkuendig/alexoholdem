package ao.holdem.history_game;

import ao.holdem.model.act.RealAction;
import ao.persist.PlayerHandle;
import ao.holdem.history.state.RunningState;
import ao.holdem.history.state.StatePlayer;

import java.util.Map;

/**
 *
 */
public class RealDealer
{
    //--------------------------------------------------------------------
    private final RunningState                   start;
    private final Map<PlayerHandle, StatePlayer> players;


    //--------------------------------------------------------------------
    public RealDealer(RunningState startFrom,
                      Map<PlayerHandle, StatePlayer> brains)
    {
        start   = startFrom;
        players = brains;
    }


    //--------------------------------------------------------------------
    public RunningState playOutHand()
    {
        RunningState state = start.continueFrom();
        do
        {
            if (state.roundJustChanged())
            {
                switch (state.head().round())
                {
                    case FLOP:  state.cards().flop();  break;
                    case TURN:  state.cards().turn();  break;
                    case RIVER: state.cards().river(); break;
                }
            }
            
            PlayerHandle player = state.nextToAct();
            RealAction   act    = players.get( player ).act( state );

//            System.out.println(player + ", " +
//                               state.head().round() +
//                               ", act: " + act);
            state.advance( act );
        }
        while ( !state.winnersKnown() );
        return state;
    }
}

