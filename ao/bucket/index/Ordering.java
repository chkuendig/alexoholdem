package ao.bucket.index;

import ao.holdem.model.card.Suit;

import java.util.Arrays;

/**
 *
 */
public class Ordering
{
    //--------------------------------------------------------------------
    private final Suit[][] ORDER;


    //--------------------------------------------------------------------
    public static Ordering pair(Suit aheadA, Suit aheadB)
    {
        return new Ordering(
                new Suit[][]{{aheadA, aheadB},
                             complement(aheadA, aheadB)});
    }
    public static Ordering suited(Suit ahead)
    {
        return new Ordering(
                new Suit[][]{{ahead},
                             complement(ahead)});
    }
    public static Ordering unsuited(Suit ahead, Suit behind)
    {
        return new Ordering(
                new Suit[][]{{ahead}, {behind},
                             complement(ahead, behind)});
    }


    //--------------------------------------------------------------------
    private Ordering(Suit order[][])
    {
        ORDER = order;
    }
//    public Ordering(Suit... ahead)
//    {
//        assert ahead != null &&
//               ahead.length != 0 &&
//               ahead.length != 4;
//
//        ORDER = new Suit[2][];
//        ORDER[0] = ahead;
//
//        EnumSet<Suit> behind = EnumSet.allOf(Suit.class);
//        for (Suit s : ahead) behind.remove( s );
//        ORDER[1] = behind.toArray( new Suit[4 - ahead.length] );
//    }


    //--------------------------------------------------------------------
    public Ordering refine(Ordering with)
    {
        return null;
    }


    //--------------------------------------------------------------------
    public WildSuit asWild(Suit suit)
    {
        for (int i = 0; i < ORDER.length; i++)
        {
            for (Suit s : ORDER[ i ])
            {
                if (suit == s) return WildSuit.VALUES[ i ];
            }
        }
        return null;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return Arrays.deepToString(ORDER);
    }


    //--------------------------------------------------------------------
    private static Suit[] complement(Suit of)
    {
        int  index        = 0;
        Suit compliment[] = new Suit[3];
        for (Suit s : Suit.VALUES)
        {
            if (s != of)
            {
                compliment[ index++ ] = s;
            }
        }
        return compliment;
    }
    private static Suit[] complement(Suit a, Suit b)
    {
        int  index        = 0;
        Suit compliment[] = new Suit[2];
        for (Suit s : Suit.VALUES)
        {
            if (s != a && s != b)
            {
                compliment[ index++ ] = s;
            }
        }
        return compliment;
    }
}
