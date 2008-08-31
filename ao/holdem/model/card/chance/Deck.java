package ao.holdem.model.card.chance;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.util.rand.Rand;

/**
 *
 */
public class Deck
{
    //--------------------------------------------------------------------
    private final Card cards[];
    private       int  nextIndex = 0;


    //--------------------------------------------------------------------
    public Deck()
    {
        cards = Card.VALUES.clone();

        // Shuffle cards
        for (int i = cards.length; i > 1; i--)
        {
            swap(cards, i-1, Rand.nextInt(i));
        }
    }

    private Deck(Card copyCards[], int copyNextIndex)
    {
        cards     = copyCards;
        nextIndex = copyNextIndex;
    }


    //--------------------------------------------------------------------
    public Hole nextHole()
    {
        return Hole.valueOf(nextCard(), nextCard());
    }


    //--------------------------------------------------------------------
    public Community nextFlop()
    {
        return new Community(nextCard(), nextCard(), nextCard());
    }


    //--------------------------------------------------------------------
    public Card nextCard()
    {
        assert nextIndex < cards.length;

        return cards[ nextIndex++ ];
//        int swapRange = cards.length - nextIndex;
//        int destIndex = swapRange - 1;
//        swap(cards, destIndex, Rand.nextInt(swapRange));
//
//        nextIndex++;
//        return cards[ destIndex ];
    }


    //--------------------------------------------------------------------
    public void reset()
    {
        nextIndex = 0;
    }


    //--------------------------------------------------------------------
    public Deck prototype()
    {
        return new Deck(cards, nextIndex);
    }


    //--------------------------------------------------------------------
    private static void swap(Card[] arr, int i, int j)
    {
        Card tmp;

        tmp    = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
