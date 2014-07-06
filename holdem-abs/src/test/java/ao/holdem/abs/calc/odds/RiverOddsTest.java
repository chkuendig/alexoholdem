package ao.holdem.abs.calc.odds;

import ao.holdem.abs.odds.agglom.OddFinder;
import ao.holdem.abs.odds.agglom.impl.PreciseHeadsUpOdds;
import ao.holdem.canon.flop.Flop;
import ao.holdem.canon.river.River;
import ao.holdem.canon.turn.Turn;
import ao.holdem.abs.odds.Odds;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.chance.Deck;

/**
 *
 */
public class RiverOddsTest
{
    public static void main(String[] args) {
        OddFinder oddFinder = new PreciseHeadsUpOdds();

        for (long r = 0; r < River.CANONS; r++) {
            Deck deck = new Deck();
//            if (r % 10000 == 0) {
                //River river = RiverExamples.examplesOf(r).get(0);
//                River river = new Flop(
//                        deck.nextCard(), deck.nextCard(),
//                        deck.nextCard(), deck.nextCard(), deck.nextCard())
//                        .addTurn(deck.nextCard())
//                        .addRiver(deck.nextCard());
            River river = new Flop(
                    Card.TWO_OF_CLUBS, Card.TWO_OF_DIAMONDS,
                    Card.EIGHT_OF_HEARTS, Card.NINE_OF_HEARTS, Card.TEN_OF_SPADES)
                    .addTurn(Card.FIVE_OF_CLUBS)
                    .addRiver(Card.SIX_OF_CLUBS);

//            River river = new Flop(
//                        Card.ACE_OF_SPADES, Card.FOUR_OF_HEARTS,
//                        deck.nextCard(), deck.nextCard(), deck.nextCard())
//                        .addTurn(deck.nextCard())
//                        .addRiver(deck.nextCard());

                Turn turn = river.turn();
                Flop flop = turn.flop();

                Odds odds = oddFinder.compute(
                        flop.hole().reify(),
                        new Community(
                                flop.flopA(), flop.flopB(), flop.flopC(),
                                turn.turnRealCard(),
                                river.riverRealCard()),
                        1);

                System.out.println(river + " | " + odds.strengthVsRandom());
            }
//        }
    }
}
