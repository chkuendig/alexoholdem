package ao.holdem.abs.calc;

import ao.holdem.abs.bucket.index.detail.river.ProbabilityEncoding;
import ao.holdem.abs.odds.agglom.OddFinder;
import ao.holdem.abs.odds.Odds;
import ao.holdem.abs.odds.agglom.impl.PreciseHeadsUpOdds;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.util.math.stats.Combiner;

import java.util.Arrays;

/**
 * 13/02/14 6:44 PM
 */
public class ProbabilityEncodingTest
{
    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        double[] codingToProb = new double[ ProbabilityEncoding.COUNT ];
        Arrays.fill(codingToProb, Double.NaN);

        int    errCount  = 0;
        double mrsError  = 0;

        OddFinder oddFinder = new PreciseHeadsUpOdds();
        for (Card[] hand : new Combiner<>(Card.VALUES, 7))
        {
            Hole hole = Hole.valueOf(hand[0], hand[1]);
            Community community = new Community(
                    hand[2], hand[3], hand[4], hand[5], hand[6]);

            Odds odds = oddFinder.compute(hole, community, 1);
            double winProb = odds.strengthVsRandom();

            char   coding    = ProbabilityEncoding.encodeWinProb(winProb);
            double codedProb = codingToProb[ coding ];
            if (Double.isNaN(codedProb)) {
                codingToProb[ coding ] = winProb;
            } else {
                double delta = codedProb - winProb;
                mrsError += delta * delta;
                errCount++;
            }
        }

        System.out.println("Mean root squared error: " + Math.sqrt(mrsError / errCount));
    }
}
