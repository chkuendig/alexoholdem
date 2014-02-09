package ao.holdem.canon;

import ao.holdem.canon.hole.CanonHole;
import ao.holdem.canon.river.River;
import ao.holdem.model.card.chance.Deck;

/**
 * 09/02/14 4:56 PM
 */
public class CanonPreCalc
{
    public static void main(String[] args) {
        Deck deck = new Deck();

        River river = CanonHole
                .create(deck.nextHole())
                .addFlop(deck.nextFlop())
                .addTurn(deck.nextCard())
                .addRiver(deck.nextCard());

        System.out.println("Random hand: " + river);
    }
}
