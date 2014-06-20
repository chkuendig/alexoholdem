package ao.holdem.bot.human;

import ao.holdem.bot.AbstractPlayer;
import ao.holdem.bot.limit_cfr.CfrBot2;
import ao.holdem.engine.state.ActionState;
import ao.holdem.model.Avatar;
import ao.holdem.model.ChipStack;
import ao.holdem.model.act.Action;
import ao.holdem.model.act.FallbackAction;
import ao.holdem.model.card.sequence.CardSequence;
import ao.holdem.abs.odds.agglom.impl.PreciseHeadsUpOdds;
import ao.util.ui.AoConsole;

import java.util.LinkedHashMap;
import java.util.Map;

//import ao.holdem.engine.analysis.Analysis;

/**
 * User: alex
 * Date: 1-Apr-2009
 * Time: 11:21:37 AM
 */
public class ConsoleBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    private Map<Avatar, ChipStack> total = new LinkedHashMap<Avatar, ChipStack>();


    //--------------------------------------------------------------------
    public void handEnded(Map<Avatar, ChipStack> deltas)
    {
        if (total.isEmpty()) {
            total.putAll( deltas );
        } else {
            for (Map.Entry<Avatar, ChipStack> delta : deltas.entrySet()) {
                total.put(
                        delta.getKey(),
                        total.get( delta.getKey() )
                             .plus( delta.getValue() ));
            }
        }

        Avatar you  = Avatar.local("you");
        int    your = deltas.get(you).smallBlinds();
        int    sum  = total .get(you).smallBlinds();
        System.out.println(
                (your > 0 ? "+" : "") + your +
                " totalling " + sum);
    }


    //--------------------------------------------------------------------
    public Action act(ActionState state,
                      CardSequence cards/*,
                      Analysis     analysis*/)
    {
//        System.out.println(
//                state.toString());
        System.out.println(
                cards                + "   " +
                winPercentage(cards) + " " +
                CfrBot2.handType(cards));

        FallbackAction act;
        do {
            act = ask( state );
        } while (act == null);
        System.out.println();

        return state.reify( act );
    }

    private String winPercentage(CardSequence cards)
    {
        double strength =
                new PreciseHeadsUpOdds().compute(
                        cards.hole(),
                        cards.community()
                ).strengthVsRandom();

        return "" +
                  ((int) (strength * 100)) +
               "% to win";
    }

    
    //--------------------------------------------------------------------
    private FallbackAction ask(ActionState state)
    {
        StringBuilder message = new StringBuilder();

        if (state.canCheck()) {
            message.append("[1] Check");
        } else {
            message.append("[1] Fold ");
        }

        if (state.toCall().equals( ChipStack.ZERO ))
        {
            message.append("     [2] Check ");
        }
        else
        {
            message.append("     [2] Call ")
                   .append(state.toCall().smallBlinds());
        }

        if (state.canRaise())
        {
            if (state.remainingBetsInRound() == 4)
            {
                message.append("     [3] Bet  ");
            }
            else
            {
                message.append("     [3] Raise");
            }      
        }
        else
        {
            message.append("     [3] Call ");
        }
        message.append(" ")
                .append( state.betSize().smallBlinds() );

        message.append("\t\tpot = ")
               .append(state.pot().smallBlinds())
               .append(", stakes = ")
               .append(state.stakes().smallBlinds());

        FallbackAction act = null;
        String         in  = AoConsole.text(message.toString());
        if (in.equals("1") || in.equalsIgnoreCase("f")) {
            act = FallbackAction.CHECK_OR_FOLD;
        } else if (in.equals("2") || in.equalsIgnoreCase("c")) {
            act = FallbackAction.CHECK_OR_CALL;
        } else if (in.equals("3") ||
                   in.equalsIgnoreCase("b") || in.equalsIgnoreCase("r")) {
            act = FallbackAction.RAISE_OR_CALL;
        }
        return act;
    }
}
