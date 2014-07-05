package ao.holdem.engine.eval.odds;


import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OddsBy5TurnTest
{
    private static final double epsilon = 0.02;


    @Test
    public void hole5hJsFlop7c4h2dTurn3c() {
        double actual = OddsBy5.INSTANCE.approximateHeadsUpHandStrength(new CardSequence(
                Hole.valueOf(Card.FIVE_OF_HEARTS, Card.JACK_OF_SPADES),
                new Community(Card.SEVEN_OF_CLUBS, Card.FOUR_OF_HEARTS, Card.TWO_OF_DIAMONDS,
                        Card.THREE_OF_CLUBS)));

        assertEquals(0.31964, actual, epsilon);
    }


    @Test
    public void hole9sJcFlopTd5d9dTurn4s() {
        double actual = OddsBy5.INSTANCE.approximateHeadsUpHandStrength(new CardSequence(
                Hole.valueOf(Card.NINE_OF_SPADES, Card.JACK_OF_CLUBS),
                new Community(Card.TEN_OF_DIAMONDS, Card.FIVE_OF_DIAMONDS, Card.NINE_OF_DIAMONDS,
                        Card.FOUR_OF_SPADES)));

        assertEquals(0.64754, actual, epsilon);
    }


    @Test
    public void holeQs2hFlopQcQdQhTurn4s() {
        double actual = OddsBy5.INSTANCE.approximateHeadsUpHandStrength(new CardSequence(
                Hole.valueOf(Card.QUEEN_OF_SPADES, Card.TWO_OF_HEARTS),
                new Community(Card.QUEEN_OF_CLUBS, Card.QUEEN_OF_DIAMONDS, Card.QUEEN_OF_HEARTS,
                        Card.FOUR_OF_SPADES)));

        assertEquals(1.0, actual, epsilon);
    }
}
