package ao.ai.monte_carlo;

import ao.holdem.model.act.RealAction;
import ao.persist.PlayerHandle;
import ao.state.StateManager;

import java.util.Map;

/**
 *
 */
public class Simulator
{
    //--------------------------------------------------------------------
    private final StateManager start;
    private final Map<PlayerHandle, PredictorBot> players;


    //--------------------------------------------------------------------
    public Simulator(StateManager startFrom,
                     Map<PlayerHandle, PredictorBot> brains)
    {
        start   = startFrom;
        players = brains;
    }


    //--------------------------------------------------------------------
    public StateManager playOutHand()
    {
        StateManager state = start.prototype();
        do
        {
            PlayerHandle player = state.nextToAct();
            RealAction   act    = players.get( player ).act( state );

            state.advance(  act   );
        }
        while ( !state.winnersKnown() );

        state.head().unfolded();


        return state;
    }
}
