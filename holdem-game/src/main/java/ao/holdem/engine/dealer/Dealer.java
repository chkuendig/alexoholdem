package ao.holdem.engine.dealer;

import ao.holdem.engine.Player;
import ao.holdem.engine.state.ActionStateFlow;
import ao.holdem.engine.state.Seat;
import ao.holdem.model.Avatar;
import ao.holdem.model.ChipStack;
import ao.holdem.model.act.Action;
import ao.holdem.model.card.chance.ChanceCards;
import ao.holdem.model.card.sequence.CardSequence;
import ao.holdem.model.card.sequence.LiteralCardSequence;
import ao.holdem.model.replay.Replay;
import ao.holdem.model.replay.StackedReplay;

import java.util.Collections;
import java.util.HashMap;
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
            ChanceCards cards)
    {
        ActionStateFlow stateFlow = new ActionStateFlow(
                clockwiseDealerLast.size(), autoBlinds);

        do
        {
            int playerIndex = stateFlow.head().nextToAct().player();

            CardSequence playerCards = new LiteralCardSequence(
                    cards.hole(playerIndex),
                    cards.community(stateFlow.head().round()));

            Avatar player = clockwiseDealerLast.get(playerIndex);

            Action act = brains.get(player)
                    .act(stateFlow.head(), playerCards);

            stateFlow.advance(act);
            handleQuitters(stateFlow, clockwiseDealerLast);
        }
        while (! stateFlow.head().atEndOfHand());

        List<ChipStack> deltas = stateFlow.deltas(cards);
        publishOutcome(Collections.unmodifiableList(deltas));

        Map<Avatar, ChipStack> avatarToDelta = new HashMap<>();
        for (int i = 0; i < deltas.size(); i++) {
            avatarToDelta.put(clockwiseDealerLast.get(i), deltas.get(i));
        }
        return new StackedReplay(
                Replay.fromFlow(clockwiseDealerLast, cards, stateFlow), avatarToDelta);
    }

    private void handleQuitters(ActionStateFlow stateFlow, List<Avatar> clockwiseDealerLast)
    {
        for (Seat pState : stateFlow.head().unfolded())
        {
            int playerIndex = pState.player();
            Avatar player = clockwiseDealerLast.get(playerIndex);

            if (brains.get( player ).hasQuit())
            {
                stateFlow.advanceQuitter( playerIndex );
            }
        }
    }

    private void publishOutcome(List<ChipStack> deltas)
    {
        for (Player p : brains.values()) {
            p.handEnded(deltas);
        }
    }
}
