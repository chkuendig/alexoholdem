package ao.holdem.ai.bucket.index.detail.river;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.ai.ai.odds.agglom.OddFinder;
import ao.holdem.ai.ai.odds.agglom.Odds;
import ao.holdem.ai.ai.odds.agglom.impl.PreciseHeadsUpOdds;
import ao.util.math.stats.Combiner;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * User: alex
 * Date: 2-Jun-2009
 * Time: 4:14:23 PM
 */
public class ProbabilityEncoding
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(ProbabilityEncoding.class);


    private ProbabilityEncoding() {}


    //--------------------------------------------------------------------
    public static int COUNT = Character.MAX_VALUE;
    public static int MAX   = COUNT - 1;


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        double[] codingToProb = new double[ COUNT ];
        Arrays.fill(codingToProb, Double.NaN);

        int    errCount  = 0;
        double mrsError  = 0;

        OddFinder oddFinder = new PreciseHeadsUpOdds();
        for (Card[] hand : new Combiner<Card>(Card.VALUES, 7))
        {
            Hole hole = Hole.valueOf(hand[0], hand[1]);
            Community community = new Community(
                    hand[2], hand[3], hand[4], hand[5], hand[6]);

            Odds odds = oddFinder.compute(hole, community, 1);
            double winProb = odds.strengthVsRandom();

            char   coding    = encodeWinProb(winProb);
            double codedProb = codingToProb[ coding ];
            if (Double.isNaN(codedProb)) {
                codingToProb[ coding ] = winProb;
            } else {
                double delta = codedProb - winProb;
                mrsError += delta * delta;
                errCount++;
            }
        }

        LOG.info("Mean root squared error: " +
                    Math.sqrt(mrsError / errCount));
    }


    //--------------------------------------------------------------------
    public static char encodeWinProb(double prob) {
        return (char)(MAX * prob);
    }

    //--------------------------------------------------------------------
    public static double decodeWinProb(char prob) {
        return (double) prob / MAX;
    }
}
