package ao.holdem.abs.calc.odds;

import ao.holdem.abs.bucket.index.detail.flop.FlopOdds;
import ao.holdem.abs.odds.agglom.Odds;
import ao.holdem.canon.flop.Flop;

/**
 * 13/02/14 6:24 PM
 */
public class FlopOddsTest
{
    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        long minWins = Integer.MAX_VALUE;
        long maxWins = Integer.MIN_VALUE;

        long minLose = Integer.MAX_VALUE;
        long maxLose = Integer.MIN_VALUE;

        long minTies = Integer.MAX_VALUE;
        long maxTies = Integer.MIN_VALUE;

        for (int i = 0; i < Flop.CANONS; i++)
        {
            Odds odds = FlopOdds.lookup(i);

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
