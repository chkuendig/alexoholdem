package ao.ai.human;

import ao.ai.AbstractPlayer;
import ao.holdem.engine.analysis.Analysis;
import ao.holdem.engine.state.State;
import ao.holdem.model.Avatar;
import ao.holdem.model.Chips;
import ao.holdem.model.act.AbstractAction;
import ao.holdem.model.act.Action;
import ao.holdem.model.card.sequence.CardSequence;
import ao.util.io.Console;

import java.util.Map;

/**
 * User: alex
 * Date: 1-Apr-2009
 * Time: 11:21:37 AM
 */
public class ConsoleBot extends AbstractPlayer
{
    public void handEnded(Map<Avatar, Chips> deltas) {}


    public Action act(State        state,
                      CardSequence cards,
                      Analysis     analysis)
    {
        System.out.println(
                state.toString());
        System.out.println(
                cards);

        AbstractAction act;
        do {
            act = ask( state );
        } while (act == null);

        return state.reify( act.toFallbackAction() );
    }

    private AbstractAction ask(State state)
    {
        StringBuilder message = new StringBuilder("[1] Fold");

        if (state.toCall().equals( Chips.ZERO ))
        {
            message.append(", [2] Check");
        }
        else
        {
            message.append(", [2] Call");
        }

        if (state.canRaise())
        {
            if (state.remainingBetsInRound() == 4)
            {
                message.append(", [3] Bet");
            }
            else
            {
                message.append(", [3] Raise");
            }
        }

        int act = Console.integer(message.toString());
        if (! (1 <= act && act <= 3)) return null;
        if (act == 3 && !state.canRaise()) return null;

        return AbstractAction.VALUES[ act - 1 ];
    }
}
