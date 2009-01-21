package ao.bucket.index.hole;

import ao.bucket.index.card.CanonCard;
import ao.bucket.index.card.Order;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;

import java.util.Arrays;

/**
 * Date: Jan 21, 2009
 * Time: 12:44:16 PM
 */
public class HoleLookup
{
    //--------------------------------------------------------------------
    private static final int PAIR_COUNT             = 13;
    private static final int PAIR_PLUS_SUITED_COUNT = PAIR_COUNT + 78;
    public  static final int CANONICAL_COUNT        = 169;

    private HoleLookup() {}


    //--------------------------------------------------------------------
    private static final CanonHole[][] CANONS = computeCanons();


    //--------------------------------------------------------------------
    private static CanonHole[][] computeCanons()
    {
        CanonHole[][] canons = new CanonHole[52][52];

        for (Card a : Card.VALUES)
        {
            for (Card b : Card.VALUES)
            {
                if (a == b) continue;

                canons[ a.ordinal() ][ b.ordinal() ] =
                        computeCanonHole(a, b);
            }
        }

        return canons;
    }


    //--------------------------------------------------------------------
    private static CanonHole computeCanonHole(Card a, Card b)
    {
        Hole        hole       = Hole.valueOf(a, b);
        char        canonIndex = computeCanonicalIndex(hole);
        Order       order      = computeOrder(hole);
        CanonCard[] canon      = new CanonCard[]{
                                    order.asCanon(a), order.asCanon(b)};
        Arrays.sort(canon);

        return new CanonHole(hole, canonIndex, order, canon);
    }


    //--------------------------------------------------------------------
    private static char computeCanonicalIndex(Hole hole)
    {
        if (hole.paired())
        {
            return (char) hole.a().rank().ordinal();
        }
        else
        {
            int hi  = hole.hi().rank().ordinal();
            int lo  = hole.lo().rank().ordinal();

            int subIndex = hi * (hi - 1) / 2 + lo;

            return (char)
                   ((hole.suited()
                      ? PAIR_COUNT
                      : PAIR_PLUS_SUITED_COUNT) +
                     subIndex);
        }
    }

    private static Order computeOrder(Hole hole)
    {
        return hole.paired()
               ? Order.pair(hole.a().suit(), hole.b().suit())
               : hole.suited()
                 ? Order.suited  (hole.a().suit())
                 : Order.unsuited(hole.hi().suit(),
                                  hole.lo().suit());
    }


    //--------------------------------------------------------------------
    public static CanonHole lookup(Card holeCards[])
    {
        return lookup(holeCards[0], holeCards[1]);
    }
    public static CanonHole lookup(Card a, Card b)
    {
        return CANONS[ a.ordinal() ][ b.ordinal() ];
    }
}
