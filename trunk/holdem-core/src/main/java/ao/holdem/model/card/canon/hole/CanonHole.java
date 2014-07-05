package ao.holdem.model.card.canon.hole;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.canon.CanonCard;
import ao.holdem.model.card.canon.Order;
import ao.holdem.model.card.canon.base.CanonIndexed;

/**
 * Date: Jan 21, 2009
 * Time: 12:51:07 PM
 */
public class CanonHole implements CanonIndexed
{
    //--------------------------------------------------------------------
    public static final int CANONS = 169;


    //--------------------------------------------------------------------
    private final Hole        HOLE;
    private final char        CANON_INDEX;
    private final Order       ORDER;
    private final CanonCard[] CANON;


    //--------------------------------------------------------------------
    public static CanonHole create(Hole hole)
    {
        return create(hole.a(), hole.b());
    }

    public static CanonHole create(Card a, Card b)
    {
        return HoleLookup.lookup(a, b);
    }

    public static CanonHole create(int canonIndex)
    {
        return HoleLookup.lookup(canonIndex);
    }


    /*package-private*/
            CanonHole(Hole        hole,
                      char        canonIndex,
                      Order       order,
                      CanonCard[] canon)
    {
        HOLE        = hole;
        CANON_INDEX = canonIndex;
        ORDER       = order;
        CANON       = canon;
    }


    //--------------------------------------------------------------------
    public Hole reify()
    {
        return HOLE;
    }

    public boolean paired()
    {
        return HOLE.isPair();
    }

    public Card a()
    {
        return HOLE.a();
    }
    public Card b()
    {
        return HOLE.b();
    }


    //--------------------------------------------------------------------
    public char canonIndex()
    {
        return CANON_INDEX;
    }
    public long packedCanonIndex()
    {
        return canonIndex();
    }

    public Order order()
    {
        return ORDER;
    }

    public CanonCard[] canonCards()
    {
        return CANON;
    }


    //--------------------------------------------------------------------
    public CanonCard[] asWild(Order refineWith)
    {
        return asWild(CANON, refineWith);
    }
    public CanonCard[] asWild(
            CanonCard canon[],
            Order     refineWith)
    {
        if (!(canon[0].isWild() ||
              canon[1].isWild())) return canon;

        CanonCard refinedA = refineWith.asCanon(HOLE.a());
        CanonCard refinedB = refineWith.asCanon(HOLE.b());

        return (refinedA.ordinal() < refinedB.ordinal())
               ? new CanonCard[] {refinedA, refinedB}
               : new CanonCard[] {refinedB, refinedA};
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return HOLE.toString();
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CanonHole canonHole = (CanonHole) o;
        return CANON_INDEX == canonHole.CANON_INDEX;
    }

    @Override public int hashCode()
    {
        return (int) CANON_INDEX;
    }
}
