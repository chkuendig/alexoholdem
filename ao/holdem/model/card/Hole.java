package ao.holdem.model.card;

import ao.holdem.model.card.Card;

import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import java.io.Serializable;


/**
 *
 */
@Embeddable
public class Hole implements Serializable
{
    //--------------------------------------------------------------------
    private Card FIRST;
    private Card SECOND;


    //--------------------------------------------------------------------
    public Hole(Card first, Card second)
    {
        FIRST  = (first != null ? first : second);
        SECOND = (first == null ? null  : second);
    }


    //--------------------------------------------------------------------
    public Hole()
    {
        this(null, null);
    }

    @Enumerated
    public Card getFirstCard()
    {
        return FIRST;
    }
    public void setFirstCard(Card first)
    {
        FIRST = first;
    }

    @Enumerated
    public Card getSecondCard()
    {
        return SECOND;
    }
    public void setSecondCard(Card second)
    {
        SECOND = second;
    }


    //--------------------------------------------------------------------
    public boolean ranks(Card.Rank rankA, Card.Rank rankB)
    {
        return FIRST.rank() == rankA && SECOND.rank() == rankB ||
               FIRST.rank() == rankB && SECOND.rank() == rankA;
    }
    
    public boolean ranks(Card.Rank rank)
    {
        return FIRST.rank() == rank || SECOND.rank() == rank;
    }

    public boolean suited()
    {
        return FIRST.suit() == SECOND.suit();
    }

    public boolean hasXcard()
    {
        return FIRST.rank().ordinal() < Card.Rank.JACK.ordinal() ||
                SECOND.rank().ordinal() < Card.Rank.JACK.ordinal();
    }

    public boolean contains(Card card)
    {
        return FIRST == card || SECOND == card;
    }


    //--------------------------------------------------------------------
    public Card first()
    {
        return FIRST;
    }

    public Card second()
    {
        return SECOND;
    }


    //--------------------------------------------------------------------
    public boolean incomplete()
    {
        return !bothCardsVisible();
    }
    public boolean bothCardsVisible()
    {
        return FIRST != null && SECOND != null;
    }

//    public boolean aCardIsVisible()
//    {
//        return FIRST != null || SECOND != null;
//    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return "[" + FIRST + ", " + SECOND + "]";
    }
}
