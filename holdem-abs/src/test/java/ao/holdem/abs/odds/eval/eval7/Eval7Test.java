package ao.holdem.abs.odds.eval.eval7;

import ao.holdem.engine.state.eval.Eval5;
import ao.holdem.engine.state.eval.HandRank;
import ao.holdem.model.card.Card;
import ao.util.math.stats.Combiner;
import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 09/02/14 5:42 PM
 */
public class Eval7Test
{


//    High Card = 23294460
//    Pair = 58627800
//    Two Pair = 31433400
//    Three of a Kind = 6461620
//    Straight = 6180020
//    Flush = 4047644
//    Full House = 3473184
//    Four of a Kind = 224848
//    Straight Flush = 41584
//    Total Hands = 133784560


    // http://archives1.twoplustwo.com/showthreaded.php?Cat=0&Number=8554678&page=0&vc=1
    @Test
    public void handRankAreCounted()
    {
        Multiset<HandRank> histogram = Eval7Faster.computeAll();

        assertEquals(41_584, histogram.count(HandRank.STRAIGHT_FLUSH));
        assertEquals(224_848, histogram.count(HandRank.FOUR_OF_A_KIND));
        assertEquals(3_473_184, histogram.count(HandRank.FULL_HOUSE));
        assertEquals(4_047_644, histogram.count(HandRank.FLUSH));
        assertEquals(6_180_020, histogram.count(HandRank.STRAIGHT));
        assertEquals(6_461_620, histogram.count(HandRank.THREE_OF_A_KIND));
        assertEquals(31_433_400, histogram.count(HandRank.TWO_PAIR));
        assertEquals(58_627_800, histogram.count(HandRank.ONE_PAIR));
        assertEquals(23_294_460, histogram.count(HandRank.HIGH_CARD));
    }
}
