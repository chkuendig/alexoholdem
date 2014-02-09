package ao.holdem.canon.hole;

import ao.holdem.canon.CanonIndexed;
import ao.holdem.canon.card.CanonCard;
import ao.holdem.canon.card.Order;
import ao.holdem.canon.flop.Flop;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;

/**
 * Date: Jan 21, 2009
 * Time: 12:51:07 PM
 */
public class CanonHole implements CanonIndexed
{
    //--------------------------------------------------------------------
    public  static final int CANONS = 169;


    //--------------------------------------------------------------------
    private final Hole        HOLE;
    private final char        CANON_INDEX;
    private final Order       ORDER;
    private final CanonCard[] CANON;


    //--------------------------------------------------------------------
    public static CanonHole create(Hole hole)
    {
        return create(hole.a(), hole.a());
    }

    public static CanonHole create(Card a, Card b)
    {
        return HoleLookup.lookup(a, b);
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
    public Flop addFlop(Community community)
    {
        return new Flop(this, community);
    }
    public Flop addFlop(Card flopA, Card flopB, Card flopC)
    {
        return new Flop(this, flopA, flopB, flopC);
    }

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
