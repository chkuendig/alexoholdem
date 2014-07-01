package ao.holdem.abs.calc.odds;


import ao.holdem.abs.bucket.index.detail.turn.TurnOdds;
import ao.holdem.engine.eval.odds.Odds;
import ao.holdem.canon.flop.Flop;
import ao.holdem.canon.turn.Turn;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.canon.hole.CanonHole;
import ao.holdem.model.card.chance.Deck;

public class TurnOddsRandomTest
{
    public static void main(String[] args) {
//        {
//            Turn turn = new Flop(
//                    CanonHole.create(Hole.valueOf(Card.FIVE_OF_HEARTS, Card.JACK_OF_SPADES)),
//                    Card.SEVEN_OF_CLUBS, Card.FOUR_OF_HEARTS, Card.TWO_OF_DIAMONDS
//            ).addTurn(Card.THREE_OF_CLUBS);
//
//            Odds odds = TurnOdds.lookup(turn.canonIndex());
//
//            System.out.println(turn + "\t" + odds);
//        }


        for (int i = 0; i < 10000; i++) {
            Deck deck = new Deck();

            Turn turn = new Flop(
                    CanonHole.create(Hole.valueOf(deck.nextCard(), deck.nextCard())),
                    deck.nextCard(), deck.nextCard(), deck.nextCard()
            ).addTurn(deck.nextCard());

            Odds odds = TurnOdds.lookup(turn.canonIndex());

            System.out.println(turn + "\t" + odds);
        }
    }
}
