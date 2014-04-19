package ao.holdem.engine.dealer;

import ao.holdem.engine.Player;
import ao.holdem.engine.state.Seat;
import ao.holdem.engine.state.StateFlow;
import ao.holdem.model.Avatar;
import ao.holdem.model.ChipStack;
import ao.holdem.model.act.Action;
import ao.holdem.model.card.chance.ChanceCards;
import ao.holdem.model.card.sequence.LiteralCardSequence;
import ao.holdem.model.replay.StackedReplay;

import java.util.Collections;
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
        do
        {
            Avatar player = stateFlow.head().nextToAct().player();
            Action act    =
                    brains.get( player ).act(
                            stateFlow.head(),
                            new LiteralCardSequence(
                                    cards.hole( player ),
                                    cards.community(
                                            stateFlow.head().round() )
                            ));

            stateFlow.advance(act);
            handleQuitters( stateFlow );
        }
        while (! stateFlow.head().atEndOfHand());

        Map<Avatar, ChipStack> deltas = stateFlow.deltas(cards);
        publishOutcome(
                Collections.unmodifiableMap( deltas ));
        return new StackedReplay(
                    stateFlow.asReplay(cards),
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

    private void publishOutcome(Map<Avatar, ChipStack> deltas)
    {
        for (Avatar avatar : deltas.keySet())
        {
            brains.get( avatar )
                    .handEnded( deltas );
        }
    }
}
