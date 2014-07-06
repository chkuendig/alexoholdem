package ao.holdem.ai.odds;


import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;
import org.junit.Assert;
import org.junit.Test;

public class OddsBy5RiverTest
{
    private static final double epsilon = 0.001;


    @Test
    public void hole2c2dFlop2h8s9sTurnJhRiver2s() {
        double actual = OddsBy5.INSTANCE.approximateHeadsUpHandStrength(new CardSequence(
                Hole.valueOf(Card.TWO_OF_CLUBS, Card.TWO_OF_DIAMONDS),
                new Community(Card.TWO_OF_HEARTS, Card.EIGHT_OF_SPADES, Card.NINE_OF_SPADES,
                        Card.JACK_OF_HEARTS, Card.TWO_OF_SPADES)));

        Assert.assertEquals(1.0, actual, epsilon);
    }


    @Test
    public void hole2c2dFlop8h9hTsTurn5cRiver6c() {
        double actual = OddsBy5.INSTANCE.approximateHeadsUpHandStrength(new CardSequence(
                Hole.valueOf(Card.TWO_OF_CLUBS, Card.TWO_OF_DIAMONDS),
                new Community(Card.EIGHT_OF_HEARTS, Card.NINE_OF_HEARTS, Card.TEN_OF_SPADES,
                        Card.FIVE_OF_CLUBS, Card.SIX_OF_CLUBS)));

        Assert.assertEquals(0.27525252525252525, actual, epsilon);
    }


    // [4s, Js]	Jh, Ac, Ks	Ts	Qd | 0.5
    @Test
    public void hole4sJsFlopJhAcKsTurnTsRiverQd() {
        double actual = OddsBy5.INSTANCE.approximateHeadsUpHandStrength(new CardSequence(
                Hole.valueOf(Card.FOUR_OF_SPADES, Card.JACK_OF_SPADES),
                new Community(Card.JACK_OF_HEARTS, Card.ACE_OF_CLUBS, Card.KING_OF_SPADES,
                        Card.TEN_OF_SPADES, Card.QUEEN_OF_DIAMONDS)));

        Assert.assertEquals(0.5, actual, epsilon);
    }
}
