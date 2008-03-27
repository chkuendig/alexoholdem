package ao.holdem.engine.dealer;

import ao.holdem.engine.Player;
import ao.holdem.engine.state.Seat;
import ao.holdem.engine.state.StateFlow;
import ao.holdem.model.Avatar;
import ao.holdem.model.Chips;
import ao.holdem.model.act.Action;
import ao.holdem.model.card.chance.ChanceCards;
import ao.holdem.model.replay.StackedReplay;

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
    public StackedReplay play(
            List<Avatar> clockwiseDealerLast,
            ChanceCards  cards)
    {
        StateFlow stateFlow = new StateFlow(clockwiseDealerLast,
                                            autoBlinds);
        while (true)
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
            if (! stateFlow.head().atEndOfHand())
            {
                handleQuitters( stateFlow );
            }
            else break;
        }

        Map<Avatar, Chips> deltas = stateFlow.deltas(cards);
        publishOutcome( deltas );
        return new StackedReplay(
                    stateFlow.asHand( cards ),
                    deltas);
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

    private void publishOutcome(Map<Avatar, Chips> deltas)
    {
        for (Avatar avatar : deltas.keySet())
        {
            brains.get( avatar )
                    .handEnded( deltas );
        }
    }
}
