package ao.holdem.abs.bucket.index.detail.river;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.abs.odds.agglom.OddFinder;
import ao.holdem.abs.odds.agglom.Odds;
import ao.holdem.abs.odds.agglom.impl.PreciseHeadsUpOdds;
import ao.util.math.stats.Combiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Date: 2-Jun-2009
 * Time: 4:14:23 PM
 */
public enum ProbabilityEncoding
{;
    //--------------------------------------------------------------------
    public static int COUNT = Character.MAX_VALUE;
    public static int MAX   = COUNT - 1;


    //--------------------------------------------------------------------
    public static char encodeWinProb(double prob) {
        return (char)(MAX * prob);
    }

    //--------------------------------------------------------------------
    public static double decodeWinProb(char prob) {
        return (double) prob / MAX;
    }
}
