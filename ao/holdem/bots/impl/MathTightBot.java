package ao.holdem.bots.impl;

import ao.holdem.bots.util.ApproximateOddFinder;
import ao.holdem.bots.util.OddFinder;
import ao.holdem.bots.util.Odds;
import ao.holdem.def.bot.AbstractBot;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.model.cards.Community;
import ao.holdem.def.state.action.Action;
import ao.holdem.def.state.env.Environment;

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
        
        Hole      hole      = env.hole();
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