package ao.bucket.index.iso_cards;

import ao.holdem.model.card.Rank;

/**
 *
 */
public class WildCard
{
    //--------------------------------------------------------------------
    private final Rank     RANK;
    private final WildSuit SUIT;


    //--------------------------------------------------------------------
    public WildCard(Rank rank, WildSuit suit)
    {
        RANK = rank;
        SUIT = suit;
    }


    //--------------------------------------------------------------------
    public Rank rank()
    {
        return RANK;
    }

    public WildSuit suit()
    {
        return SUIT;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return RANK + " of " + SUIT;
    }


    //--------------------------------------------------------------------
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WildCard wildCard = (WildCard) o;

        return RANK == wildCard.RANK &&
               SUIT == wildCard.SUIT;
    }

    public int hashCode()
    {
        int result;
        result = RANK.hashCode();
        result = 31 * result + SUIT.hashCode();
        return result;
    }
}
