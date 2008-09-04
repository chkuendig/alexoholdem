package ao.bucket.index.iso_cards.wild.card;

import ao.bucket.index.iso_cards.wild.suit.WildSuitMarker;
import ao.holdem.model.card.Rank;

/**
 * Date: Aug 27, 2008
 * Time: 3:29:37 AM
 */
public class RankedSuitedImpl<S extends WildSuitMarker<S>>
        implements RankedSuited<Rank, S>,
                   Comparable<RankedSuited<Rank, S>>
{
    //--------------------------------------------------------------------
    private final Rank RANK;
    private final S    SUIT;


    //--------------------------------------------------------------------
    protected RankedSuitedImpl(Rank rank,
                               S    suit)
    {
        RANK = rank;
        SUIT = suit;
    }


    //--------------------------------------------------------------------
    public Rank rank()
    {
        return RANK;
    }

    public S suit()
    {
        return SUIT;
    }


    //--------------------------------------------------------------------
    public int compareTo(RankedSuited<Rank, S> b)
    {
        int suitCmp = SUIT.compareTo( b.suit() );
        return (suitCmp == 0
                   ? RANK.compareTo( b.rank() )
                   : suitCmp);
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
        if (!(o instanceof RankedSuitedImpl)) return false;

        RankedSuitedImpl that = (RankedSuitedImpl) o;

        return RANK == that.RANK &&
               SUIT == that.SUIT;
    }

    public int hashCode()
    {
        int result;
        result = RANK.hashCode();
        result = 31 * result + SUIT.hashCode();
        return result;
    }
}
