package ao.holdem.engine;

import ao.holdem.model.Player;
import ao.holdem.model.act.RealAction;
import ao.persist.PlayerHandle;
import ao.state.PlayerState;
import ao.state.StateManager;

import java.util.Map;

/**
 *
 */
public class Dealer
{
    //--------------------------------------------------------------------
    private final StateManager                        start;
    private final Map<PlayerHandle, ? extends Player> players;


    //--------------------------------------------------------------------
    public Dealer(StateManager startFrom,
                      Map<PlayerHandle, ? extends Player> brains)
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

//            handleQuitters(state);
            PlayerHandle player = state.nextToAct();
            RealAction   act    = players.get( player ).act( state );

            handleQuitters(state);
            state.advance( act );
        }
        while ( !state.winnersKnown() );
        
        if (state.winners().isEmpty())
            throw new HoldemRuleBreach("winnerless hand");
        return state;
    }

    private void handleQuitters(StateManager state)
    {
        for (PlayerState pState : state.head().unfolded())
        {
            if (players.get( pState.handle() ).shiftQuitAction())
            {
                state.advanceQuitter( pState.handle() );
            }
        }
    }
}

