package ao.holdem.abs.calc.odds;

import ao.holdem.abs.bucket.index.detail.DetailLookup;
import ao.holdem.abs.bucket.index.detail.flop.FlopDetailFlyweight;
import ao.holdem.abs.bucket.index.detail.flop.FlopOdds;
import ao.holdem.canon.flop.Flop;
import ao.holdem.engine.eval.odds.Odds;
import ao.holdem.engine.eval.odds.OddsBy5;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.sequence.CardSequence;

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

        double maxDelta = 0;
        for (int i = 0; i < Flop.CANONS; i++)
        {
            Odds odds = FlopOdds.lookup(i);

            //if (i % 1000 == 0) {
                FlopDetailFlyweight.CanonFlopDetail detail = DetailLookup.lookupFlop(i);
                Flop exampleFlop = new Flop(
                        detail.holeDetail().canon(),
                        detail.a(), detail.b(), detail.c());
                //System.out.println(exampleFlop + "\t" + odds);

                double approx = OddsBy5.INSTANCE.approximateHeadsUpHandStrength(new CardSequence(
                        exampleFlop.hole().reify(),
                        new Community(exampleFlop.flopA(), exampleFlop.flopB(), exampleFlop.flopC())));
                double delta = odds.strengthVsRandom() - approx;

                if (Math.abs(maxDelta) < Math.abs(delta)) {
                    System.out.println(exampleFlop + "\t" + odds.strengthVsRandom() + "\t" + approx + "\t" +
                            Math.round(odds.strengthVsRandom() * 100) + "\t" + Math.round(approx * 100) + "\t" + delta);
                    maxDelta = delta;
                }
            //}

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
