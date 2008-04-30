package ao.bucket.index;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;

/**
 *
 */
public class IsoHole
{
    //--------------------------------------------------------------------
    private final Hole     HOLE;
    private final Ordering ORDER;

    private final WildCard A, B;


    //--------------------------------------------------------------------
    public IsoHole(Hole hole)
    {
        Card a = Card.BY_RANK.max(hole.a(), hole.b());
        Card b = Card.BY_RANK.min(hole.a(), hole.b());

        HOLE  = Hole.newInstance(a, b);
        ORDER = hole.paired()
                ? Ordering.pair(a.suit(), b.suit())
                : hole.suited()
                  ? Ordering.suited  (a.suit())
                  : Ordering.unsuited(a.suit(), b.suit());

        A = new WildCard(a.rank(), ORDER.asWild(a.suit()));
        B = new WildCard(b.rank(), ORDER.asWild(b.suit()));
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return HOLE + " | " + ORDER;
    }


    //--------------------------------------------------------------------
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IsoHole isoHole = (IsoHole) o;

        return A.equals(isoHole.A) &&
               B.equals(isoHole.B);
    }

    public int hashCode()
    {
        int result;
        result = A.hashCode();
        result = 31 * result + B.hashCode();
        return result;
    }
}
