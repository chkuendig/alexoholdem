package ao.holdem.def.model.cards;

import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.cards.community.Flop;
import ao.util.rand.Rand;

/**
 * Random deck of cards.
 */
public class Deck
{
    //--------------------------------------------------------------------
    private final Card cards[];
    private       int  nextIndex = 0;


    //--------------------------------------------------------------------
    public Deck()
    {
        cards = Card.values().clone();

        // Shuffle cards
        for (int i = cards.length; i > 1; i--)
        {
            swap(cards, i-1, Rand.nextInt(i));
        }
    }


    //--------------------------------------------------------------------
    public Hole nextHole()
    {
        return new Hole(nextCard(), nextCard());
    }


    //--------------------------------------------------------------------
    public Flop nextFlop()
    {
        return new Flop(nextCard(), nextCard(), nextCard());
    }


    //--------------------------------------------------------------------
    public Card nextCard()
    {
        assert nextIndex < cards.length;
        return cards[ nextIndex++ ];
    }


    //--------------------------------------------------------------------
    private static void swap(Card[] arr, int i, int j)
    {
        Card tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
