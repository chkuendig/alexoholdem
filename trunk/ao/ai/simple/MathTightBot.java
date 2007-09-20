package ao.ai.simple;

import ao.odds.ApproximateOddFinder;
import ao.odds.OddFinder;
import ao.odds.Odds;
import ao.holdem.def.bot.AbstractBot;
import ao.holdem.model.Hole;
import ao.holdem.model.Community;
import ao.holdem.model.act.Action;

/**
 *
 */
public class MathTightBot extends AbstractBot
{
    //--------------------------------------------------------------------
    public Action act(Environment env)
    {
//        if (env.bettingRound() == BettingRound.PREFLOP)
//        {
//            return new LooseSklanskyBot().act(env);
//        }
        
        Hole hole      = env.hole();
        Community community = env.community();

        OddFinder oddFinder = new ApproximateOddFinder();
        Odds odds = oddFinder.compute(
                        hole, community, env.activeOpponents());

        double potOdds =
                ((double) env.toCall()) /
                (env.toCall() + env.pot());

//        System.out.println(
//                "odds: " + odds +
//                " with " + hole +
//                " on " + community +
//                " vs [" + Math.round(potOdds * 100) + "] (strength)");
        Action act = odds.strengthVsRandom() > potOdds// ||
                        //Rand.nextDouble() < 0.2
                     ? Action.RAISE_OR_CALL
                     : Action.CHECK_OR_FOLD;

//        if (Rand.nextDouble() < -1)
//        {
//            return act == Action.CHECK_OR_FOLD
//                    ? Action.RAISE_OR_CALL
//                    : act;
//        }
        return act;
    }
}