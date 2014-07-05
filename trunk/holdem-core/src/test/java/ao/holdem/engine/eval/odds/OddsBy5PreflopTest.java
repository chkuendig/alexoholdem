package ao.holdem.engine.eval.odds;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class OddsBy5PreflopTest
{
    private static final double epsilon = 0.001;


    @Test
    public void preflopTwoThreeOffsuite() {
        double actual = OddsBy5.INSTANCE.approximateHeadsUpHandStrength(new CardSequence(
                Hole.valueOf(Card.TWO_OF_CLUBS, Card.THREE_OF_DIAMONDS)));

        assertEquals(0.32295, actual, epsilon);
    }


    @Test
    public void preflopTwoThreeSuited() {
        double actual = OddsBy5.INSTANCE.approximateHeadsUpHandStrength(new CardSequence(
                Hole.valueOf(Card.TWO_OF_CLUBS, Card.THREE_OF_CLUBS)));

        assertEquals(0.35995, actual, epsilon);
    }


    @Test
    public void preflopAceAce() {
        double actual = OddsBy5.INSTANCE.approximateHeadsUpHandStrength(new CardSequence(
                Hole.valueOf(Card.ACE_OF_CLUBS, Card.ACE_OF_DIAMONDS)));

        assertEquals(0.85215, actual, epsilon);
    }


    @Test
    public void preflopTenTen() {
        double actual = OddsBy5.INSTANCE.approximateHeadsUpHandStrength(new CardSequence(
                Hole.valueOf(Card.TEN_OF_CLUBS, Card.TEN_OF_DIAMONDS)));

        assertEquals(0.7499, actual, epsilon);
    }


    @Test
    public void preflopEightKingOffsuite() {
        double actual = OddsBy5.INSTANCE.approximateHeadsUpHandStrength(new CardSequence(
                Hole.valueOf(Card.EIGHT_OF_CLUBS, Card.KING_OF_DIAMONDS)));

        assertEquals(0.5602, actual, epsilon);
    }


    @Test
    public void preflopSevenTwoSuited() {
        double actual = OddsBy5.INSTANCE.approximateHeadsUpHandStrength(new CardSequence(
                Hole.valueOf(Card.SEVEN_OF_CLUBS, Card.TWO_OF_CLUBS)));

        assertEquals(0.38165, actual, epsilon);
    }


    @Test
    public void preflopAceTwoSuited() {
        double actual = OddsBy5.INSTANCE.approximateHeadsUpHandStrength(new CardSequence(
                Hole.valueOf(Card.ACE_OF_CLUBS, Card.TWO_OF_CLUBS)));

        assertEquals(0.5738, actual, epsilon);
    }


    @Test
    public void preflopKingAceSuited() {
        double actual = OddsBy5.INSTANCE.approximateHeadsUpHandStrength(new CardSequence(
                Hole.valueOf(Card.ACE_OF_SPADES, Card.KING_OF_SPADES)));

        assertEquals(0.67045, actual, epsilon);
    }
}
