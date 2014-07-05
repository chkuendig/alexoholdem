package ao.holdem.abs.calc.odds;

import ao.holdem.abs.bucket.index.detail.preflop.HoleOdds;
import ao.holdem.engine.eval.odds.Odds;
import ao.holdem.engine.eval.odds.OddsBy5;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.canon.hole.CanonHole;
import ao.holdem.model.card.sequence.CardSequence;

/**
 * 13/02/14 6:26 PM
 */
public class HoleOddsTest {
    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        long minWins = Integer.MAX_VALUE;
        long maxWins = Integer.MIN_VALUE;

        long minLose = Integer.MAX_VALUE;
        long maxLose = Integer.MIN_VALUE;

        long minTies = Integer.MAX_VALUE;
        long maxTies = Integer.MIN_VALUE;

        for (int i = 0; i < CanonHole.CANONS; i++)
        {
            CanonHole hole = CanonHole.create(i);
            Odds odds = HoleOdds.lookup(i);

            double approx = OddsBy5.INSTANCE.approximateHeadsUpHandStrength(
                    new CardSequence(hole.reify(), Community.PREFLOP));
            double delta = odds.strengthVsRandom() - approx;
            System.out.println(hole + "\t" + odds.strengthVsRandom() + "\t" + approx + "\t" + delta);
            //System.out.println(hole + "\t" + odds);

            minWins = Math.min(minWins, odds.winOdds());
            maxWins = Math.max(maxWins, odds.winOdds());

            minLose = Math.min(minLose, odds.loseOdds());
            maxLose = Math.max(maxLose, odds.loseOdds());

            minTies = Math.min(minTies, odds.splitOdds());
            maxTies = Math.max(maxTies, odds.splitOdds());
        }

        System.out.println("minWins = " + minWins);
        System.out.println("maxWins = " + maxWins);

        System.out.println("minLose = " + minLose);
        System.out.println("maxLose = " + maxLose);

        System.out.println("minTies = " + minTies);
        System.out.println("maxTies = " + maxTies);
    }
}
