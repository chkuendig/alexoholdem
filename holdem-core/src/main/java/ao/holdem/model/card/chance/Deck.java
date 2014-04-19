package ao.holdem.model.card.chance;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;

import java.util.*;

/**
 *
 */
public class Deck
{
    //--------------------------------------------------------------------
    private final List<Card> cards;
    private       int        nextIndex = 0;


    //--------------------------------------------------------------------
    public Deck() {
        this(new Random());
    }
    public Deck(Random rand)
    {
        cards = new ArrayList<>(Arrays.asList(Card.VALUES));
        Collections.shuffle(cards, rand);
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
        assert nextIndex < cards.size();
        return cards.get( nextIndex++ );
    }


    //--------------------------------------------------------------------
    public void reset()
    {
        nextIndex = 0;
    }

    public int cardsDealt()
    {
        return nextIndex;
    }
}
