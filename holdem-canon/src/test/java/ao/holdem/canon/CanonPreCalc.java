package ao.holdem.canon;

import ao.holdem.canon.flop.Flop;
import ao.holdem.canon.river.River;
import ao.holdem.model.card.canon.hole.CanonHole;
import ao.holdem.model.card.chance.Deck;

/**
 * 09/02/14 4:56 PM
 */
public class CanonPreCalc
{
    public static void main(String[] args) {
        Deck deck = new Deck();

        River river = new Flop(
                CanonHole.create(deck.nextHole()),
                deck.nextFlop())
                .addTurn(deck.nextCard())
                .addRiver(deck.nextCard());

        System.out.println("Random hand: " + river);
    }
}
