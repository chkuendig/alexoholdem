package ao.holdem.engine;

import ao.holdem.model.act.RealAction;
import ao.persist.PlayerHandle;
import ao.state.StateManager;
import ao.holdem.model.Player;

import java.util.Map;

/**
 *
 */
public class Dealer
{
    //--------------------------------------------------------------------
    private final StateManager start;
    private final Map<PlayerHandle, Player> players;


    //--------------------------------------------------------------------
    public Dealer(StateManager startFrom,
                      Map<PlayerHandle, Player> brains)
    {
        start   = startFrom;
        players = brains;
    }


    //--------------------------------------------------------------------
    public StateManager playOutHand()
    {
        StateManager state = start.continueFrom();
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

