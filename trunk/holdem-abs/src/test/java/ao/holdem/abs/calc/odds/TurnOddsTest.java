package ao.holdem.abs.calc.odds;

import ao.holdem.abs.bucket.index.detail.turn.TurnOdds;
import ao.holdem.engine.eval.odds.Odds;
import ao.holdem.canon.turn.Turn;

/**
 * 13/02/14 6:29 PM
 */
public class TurnOddsTest
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

        for (int i = 0; i < Turn.CANONS; i++)
        {
            Odds odds = TurnOdds.lookup(i);

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
