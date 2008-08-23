package ao.bucket.index.iso_cards;

import ao.holdem.model.card.Rank;

/**
 *
 */
public class WildMarkedCard
        implements RankedSuited<Rank, WildMarkedSuit>
{
    //--------------------------------------------------------------------
    public static WildMarkedCard newInstance(
            Rank           rank,
            WildMarkedSuit suit)
    {
        return new WildMarkedCard(rank, suit);
    }


    //--------------------------------------------------------------------
    private final Rank           RANK;
    private final WildMarkedSuit SUIT;


    //--------------------------------------------------------------------
    private WildMarkedCard(Rank           rank,
                           WildMarkedSuit suit)
    {
        RANK = rank;
        SUIT = suit;
    }


    //--------------------------------------------------------------------
    public Rank rank()
    {
        return RANK;
    }

    public WildMarkedSuit suit()
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

        WildMarkedCard wildCard = (WildMarkedCard) o;

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
