package ao.holdem.model.card.canon.hole;

import ao.holdem.model.card.canon.CanonCard;
import ao.holdem.model.card.canon.Order;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;

import java.util.Arrays;

/**
 * Date: Jan 21, 2009
 * Time: 12:44:16 PM
 */
enum HoleLookup
{;
    //--------------------------------------------------------------------
    private static final int PAIR_COUNT             = 13;
    private static final int PAIR_PLUS_SUITED_COUNT = PAIR_COUNT + 78;


    //--------------------------------------------------------------------
    private static final CanonHole[]   BY_CANON = new CanonHole[ CanonHole.CANONS ];
    private static final CanonHole[][] CACHE    = computeCanons(BY_CANON);


    //--------------------------------------------------------------------
    private static CanonHole[][] computeCanons(CanonHole[] byCanon)
    {
        CanonHole[][] canons = new CanonHole[52][52];

        for (Card a : Card.VALUES)
        {
            for (Card b : Card.VALUES)
            {
                if (a == b) continue;

                CanonHole canonHole = computeCanonHole(a, b);
                canons[ a.ordinal() ][ b.ordinal() ] = canonHole;

                if (byCanon[ canonHole.canonIndex() ] == null)
                {
                    byCanon[ canonHole.canonIndex() ] = canonHole;
                }
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
        if (hole.isPair())
        {
            return (char) hole.a().rank().ordinal();
        }
        else
        {
            int hi  = hole.high().rank().ordinal();
            int lo  = hole.low().rank().ordinal();

            int subIndex = hi * (hi - 1) / 2 + lo;

            return (char)
                   ((hole.isSuited()
                      ? PAIR_COUNT
                      : PAIR_PLUS_SUITED_COUNT) +
                     subIndex);
        }
    }

    private static Order computeOrder(Hole hole)
    {
        return hole.isPair()
               ? Order.pair(hole.a().suit(), hole.b().suit())
               : hole.isSuited()
                 ? Order.suited  (hole.a().suit())
                 : Order.unsuited(hole.high().suit(),
                                  hole.low().suit());
    }


    //--------------------------------------------------------------------
    public static CanonHole lookup(Card[] holeCards)
    {
        return lookup(holeCards[0], holeCards[1]);
    }
    /*package-private*/ static CanonHole lookup(Card a, Card b)
    {
        return CACHE[ a.ordinal() ][ b.ordinal() ];
    }

    public static CanonHole lookup(int canonIndex)
    {
        return BY_CANON[ canonIndex ];
    }
}
