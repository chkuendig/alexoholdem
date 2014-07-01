package ao.holdem.engine.eval.odds;


import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.LiteralCardSequence;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


// expected amounts determined via PreciseHeadsUpOdds
public class OddsBy5FlopTest
{
    private static final double epsilon = 0.025;


    @Test
    public void hole5d2hFlop2c3d4d() {
        double actual = OddsBy5.approximateHeadsUpHandStrength(new LiteralCardSequence(
                Hole.valueOf(Card.TWO_OF_DIAMONDS, Card.TWO_OF_HEARTS),
                new Community(Card.TWO_OF_CLUBS, Card.THREE_OF_DIAMONDS, Card.FOUR_OF_DIAMONDS)));

        assertEquals(0.86883, actual, epsilon);
    }


    @Test
    public void hole2d3hFlop9s6s8s() {
        double actual = OddsBy5.approximateHeadsUpHandStrength(new LiteralCardSequence(
                Hole.valueOf(Card.TWO_OF_DIAMONDS, Card.THREE_OF_HEARTS),
                new Community(Card.NINE_OF_SPADES, Card.SIX_OF_SPADES, Card.EIGHT_OF_SPADES)));

        assertEquals(0.12435, actual, epsilon);
    }


    @Test
    public void holeKcAdFlop6cTd8h() {
        double actual = OddsBy5.approximateHeadsUpHandStrength(new LiteralCardSequence(
                Hole.valueOf(Card.KING_OF_CLUBS, Card.ACE_OF_DIAMONDS),
                new Community(Card.SIX_OF_CLUBS, Card.TEN_OF_DIAMONDS, Card.EIGHT_OF_HEARTS)));

        assertEquals(0.50135, actual, epsilon);
    }


    @Test
    public void holeQsQcFlop2cQd2s() {
        double actual = OddsBy5.approximateHeadsUpHandStrength(new LiteralCardSequence(
                Hole.valueOf(Card.QUEEN_OF_SPADES, Card.QUEEN_OF_CLUBS),
                new Community(Card.TWO_OF_CLUBS, Card.QUEEN_OF_DIAMONDS, Card.TWO_OF_SPADES)));

        assertEquals(0.99288, actual, epsilon);
    }


    @Test
    public void hole7c2dFlop4d5d8h() {
        double actual = OddsBy5.approximateHeadsUpHandStrength(new LiteralCardSequence(
                Hole.valueOf(Card.SEVEN_OF_CLUBS, Card.TWO_OF_DIAMONDS),
                new Community(Card.FOUR_OF_DIAMONDS, Card.FIVE_OF_DIAMONDS, Card.EIGHT_OF_HEARTS)));

        assertEquals(0.29974, actual, epsilon);
    }


    @Test
    public void hole8sQcFlop9cAcTd() {
        double actual = OddsBy5.approximateHeadsUpHandStrength(new LiteralCardSequence(
                Hole.valueOf(Card.NINE_OF_SPADES, Card.QUEEN_OF_CLUBS),
                new Community(Card.NINE_OF_CLUBS, Card.ACE_OF_CLUBS, Card.TEN_OF_DIAMONDS)));

        assertEquals(0.69945, actual, epsilon);
    }
}
