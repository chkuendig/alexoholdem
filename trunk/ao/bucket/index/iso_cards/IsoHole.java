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
    //private final Ordering ORDER;

    private final WildCard A, B;


    //--------------------------------------------------------------------
    public IsoHole(HoleCase holeCase,
                   WildCard a,
                   WildCard b)
    {
        CASE = holeCase;
        A    = a;
        B    = b;
    }


    //--------------------------------------------------------------------
    public IsoFlop flop(Hole hole, Card... flop)
    {
        //return new IsoFlop(ORDER, HOLE.asArray(), flop);
        return new IsoFlop(hole.ordering(), hole.asArray(), flop);
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
