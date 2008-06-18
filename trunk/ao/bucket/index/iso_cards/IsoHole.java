package ao.bucket.index.iso_cards;

import ao.bucket.index.iso_case.HoleCase;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;

/**
 *
 */
public class IsoHole
{
    //--------------------------------------------------------------------
    //private final Hole     HOLE;
    private final HoleCase CASE;
    private final Ordering ORDER;

    private final WildCard A, B;


    //--------------------------------------------------------------------
    public IsoHole(Hole hole)
    {
        Card a, b;
        if (hole.a().rank() == hole.b().rank())
        {
            a = hole.a();
            b = hole.b();
        }
        else
        {
            a = Card.BY_RANK.max(hole.a(), hole.b());
            b = Card.BY_RANK.min(hole.a(), hole.b());
        }

        //HOLE  = Hole.newInstance(a, b);
        CASE  = HoleCase.newInstance(hole);
        ORDER = hole.paired()
                ? Ordering.pair(a.suit(), b.suit())
                : hole.suited()
                  ? Ordering.suited  (a.suit())
                  : Ordering.unsuited(a.suit(), b.suit());

        A = new WildCard(a.rank(), ORDER.asWild(a.suit()));
        B = new WildCard(b.rank(), ORDER.asWild(b.suit()));
    }


    //--------------------------------------------------------------------
    public HoleCase holeCase()
    {
        return CASE;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        //return HOLE + " | " + ORDER;
        //return "[" + A + ", " + B + "] | " + ORDER;
        return CASE + " [" + A + ", " + B + "]";
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
