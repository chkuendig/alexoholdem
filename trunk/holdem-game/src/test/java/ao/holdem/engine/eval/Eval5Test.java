package ao.holdem.engine.eval;

import ao.holdem.model.card.Card;
import ao.util.math.stats.Combiner;
import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class Eval5Test
{
    // http://www.suffecool.net/poker/evaluator.html
    @Test
    public void handRankAreCounted()
    {
        Multiset<HandRank> histogram = EnumMultiset.create(HandRank.class);

        for (Card[] fiveCards : new Combiner<>(Card.VALUES, 5))
        {
            short handStrength = Eval5.valueOf(fiveCards);
            HandRank rank = HandRank.fromValue(handStrength);
            histogram.add(rank);
        }

        assertEquals(40, histogram.count(HandRank.STRAIGHT_FLUSH));
        assertEquals(624, histogram.count(HandRank.FOUR_OF_A_KIND));
        assertEquals(3_744, histogram.count(HandRank.FULL_HOUSE));
        assertEquals(5_108, histogram.count(HandRank.FLUSH));
        assertEquals(10_200, histogram.count(HandRank.STRAIGHT));
        assertEquals(54_912, histogram.count(HandRank.THREE_OF_A_KIND));
        assertEquals(123_552, histogram.count(HandRank.TWO_PAIR));
        assertEquals(1_098_240, histogram.count(HandRank.ONE_PAIR));
        assertEquals(1_302_540, histogram.count(HandRank.HIGH_CARD));
    }
}
