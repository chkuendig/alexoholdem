package ao.holdem.bots;

import ao.holdem.bots.util.ApproximateOddFinder;
import ao.holdem.bots.util.OddFinder;
import ao.holdem.bots.util.Odds;
import ao.holdem.def.bot.AbstractBot;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.model.cards.community.Community;
import ao.holdem.def.state.action.Action;
import ao.holdem.def.state.env.Environment;

import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Designed for heads up play.
 */
public class MathBot extends AbstractBot
{
    //--------------------------------------------------------------------
    public Action act(Environment env)
    {
//        if (env.opponentCount() != 1)
//        {
//            return Action.CHECK_OR_FOLD;
//        }

        Hole      hole      = env.hole();
        Community community = env.community();

//        HeadsUpOddFinder oddFinder = new HeadsUpOddFinder();
        OddFinder oddFinder = new ApproximateOddFinder();
        Odds odds = oddFinder.compute(
                        hole, community, env.activeOpponents());

        double potOdds =
                ((double) env.toCall()) /
                (env.toCall() + env.pot());

        System.out.println(
                "odds: " + odds +
                " with " + hole +
                " on " + community +
                " vs [" + Math.round(potOdds * 100) + "]");
        return (odds.winPercent() > potOdds)
                ? Action.RAISE_OR_CALL
                : Action.CHECK_OR_FOLD;
    }


    public static void main(String args[]) throws IOException
    {
        for (int i = 0; i < 10000; i++)
        {
            System.out.println();
            System.out.println("=========================");
            System.out.println("=========================");

            System.out.println("to call:");
            char toCallBytes[] = new char[20];
            new InputStreamReader(System.in).read(toCallBytes);
            String toCallStr  = new String(toCallBytes).trim();

            System.out.println("pot size:");
            char potSizeBytes[] = new char[20];
            new InputStreamReader(System.in).read(potSizeBytes);
            String potOddsStr = new String(potSizeBytes).trim();

            double toCall  = Double.valueOf(toCallStr);
            double potSize = Double.valueOf(potOddsStr);
            double potOdds = toCall / (toCall + potSize);

            System.out.println("-------------------");
            System.out.println("potOdds: " + potOdds);
        }
    }
}








