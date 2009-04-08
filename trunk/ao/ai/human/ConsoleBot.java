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
    //--------------------------------------------------------------------
    public void handEnded(Map<Avatar, Chips> deltas)
    {
        System.out.println(
                deltas);
    }


    //--------------------------------------------------------------------
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

    
    //--------------------------------------------------------------------
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

        AbstractAction act = null;
        String         in  = Console.text(message.toString());
        if (in.equals("1") || in.equalsIgnoreCase("f")) {
            act = AbstractAction.QUIT_FOLD;
        } else if (in.equals("2") || in.equalsIgnoreCase("c")) {
            act = AbstractAction.CHECK_CALL;
        } else if (in.equals("3") ||
                   in.equalsIgnoreCase("b") || in.equalsIgnoreCase("r")) {
            act = AbstractAction.BET_RAISE;
        }
        return act;
    }
}
