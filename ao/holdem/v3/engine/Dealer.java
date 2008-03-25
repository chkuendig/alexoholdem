package ao.holdem.v3.engine;

import ao.holdem.v3.engine.state.Seat;
import ao.holdem.v3.engine.state.StateFlow;
import ao.holdem.v3.model.Avatar;
import ao.holdem.v3.model.Stack;
import ao.holdem.v3.model.act.Action;
import ao.holdem.v3.model.card.chance.ChanceCards;
import ao.holdem.v3.model.hand.Replay;

import java.util.List;
import java.util.Map;

/**
 * 
 */
public class Dealer
{
    //--------------------------------------------------------------------
    private boolean                       autoBlinds;
    private Map<Avatar, ? extends Player> brains;


    //--------------------------------------------------------------------
    public Dealer(boolean                       autoPostBlinds,
                  Map<Avatar, ? extends Player> playerBrains)
    {
        autoBlinds = autoPostBlinds;
        brains     = playerBrains;
    }


    //--------------------------------------------------------------------
    public Replay play(List<Avatar> clockwiseDealerLast,
                     ChanceCards  cards)
    {
        StateFlow stateFlow = new StateFlow(clockwiseDealerLast,
                                            autoBlinds);
        do
        {
            Avatar player = stateFlow.head().nextToAct().player();
            Action act    =
                    brains.get( player ).act(
                            stateFlow.head(),
                            cards.hole( player ),
                            cards.community(
                                    stateFlow.head().round() ),
                            stateFlow.analysis());

            stateFlow.advance(act);
            handleQuitters( stateFlow );
        }
        while (! stateFlow.head().atEndOfHand());

        publishOutcome( null );
        return stateFlow.asHand( cards );
    }

    private void handleQuitters(StateFlow stateFlow)
    {
        for (Seat pState : stateFlow.head().unfolded())
        {
            Avatar player = pState.player();
            if (brains.get( player ).hasQuit())
            {
                stateFlow.advanceQuitter( player );
            }
        }
    }

    private void publishOutcome(Map<Avatar, Stack> deltas)
    {
        for (Avatar avatar : deltas.keySet())
        {
            brains.get( avatar )
                    .handEnded( deltas );
        }
    }
}
