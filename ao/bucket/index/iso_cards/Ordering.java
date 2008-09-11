package ao.bucket.index.iso_cards;

import ao.bucket.index.iso_cards.wild.suit.WildSuit;
import ao.holdem.model.card.Suit;
import ao.util.data.Arr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 *
 */
// todo: define as enum?
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
    public static Ordering triplet(
            Suit aheadA, Suit aheadB, Suit aheadC)
    {
        return new Ordering(
                new Suit[][]{{aheadA, aheadB, aheadC},
                             complement(aheadA, aheadB, aheadC)});
    }
    public static Ordering partSuited(
            Suit aheadA, Suit aheadB, Suit mid)
    {
        return new Ordering(
                new Suit[][]{{aheadA, aheadB}, {mid},
                             complement(aheadA, aheadB, mid)});
    }
    public static Ordering partSuited(
            Suit aheadA, Suit mid)
    {
        return new Ordering(
                new Suit[][]{{aheadA}, {mid},
                             complement(aheadA, mid)});
    }
    public static Ordering ordered(
            Suit ahead, Suit mid, Suit behind)
    {
        return new Ordering(
                new Suit[][]{{ahead}, {mid}, {behind},
                             complement(ahead, mid, behind)});
    }


    //--------------------------------------------------------------------
    public Ordering(Suit order[][])
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
    // todo: optimize
    public Ordering refine(Ordering with)
    {
        Suit[][] refined = new Suit[4][];

        int index = 0;
        for (Suit group[] : ORDER)
        {
            for (Suit subGroup[] : with.refine(group))
            {
                refined[ index++ ] = subGroup;
            }
        }

        return new Ordering(Arrays.copyOf(refined, index));
    }

    // todo: optimize
    private Suit[][] refine(Suit group[])
    {
        List<Suit[]> subGroups = new ArrayList<Suit[]>();

        for (Suit myGroup[] : ORDER)
        {
            int  index      = 0;
            Suit subGroup[] = new Suit[ group.length ];
            for(Suit suit : group)
            {
                if ( Arr.indexOf(myGroup, suit) != -1 )
                {
                    subGroup[ index++ ] = suit;
                }
            }

            if (index > 0)
            {
                subGroups.add( Arrays.copyOf(subGroup, index) );
            }
        }

        return subGroups.toArray(new Suit[subGroups.size()][]);
    }


    //--------------------------------------------------------------------
    public WildSuit asWild(Suit suit)
    {
        for (int i = 0; i < ORDER.length; i++)
        {
            for (Suit s : ORDER[ i ])
            {
                if (suit == s)
                {
                    return ORDER[ i ].length == 1
                           ? WildSuit.VALUES[ i ]
                           : WildSuit.WILD;
                }
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
    public boolean equals(Object o)
    {
        if (o == null || getClass() != o.getClass()) return false;

        Ordering other = (Ordering) o;
        if (ORDER.length != other.ORDER.length) return false;

        for (int i = 0; i < ORDER.length; i++)
        {
            if (ORDER[i].length != other.ORDER[i].length ||
                !Arrays.asList(ORDER[i]).containsAll(
                        Arrays.asList(other.ORDER[i]))) return false;
        }
        return true;
    }

    public int hashCode()
    {
        return Arrays.deepHashCode(ORDER);
    }

    
    //--------------------------------------------------------------------
    // todo: optimize
    private static Suit[] complement(Suit head, Suit... tail)
    {
        EnumSet<Suit> s = EnumSet.complementOf(EnumSet.of(head, tail));
        return s.toArray(new Suit[s.size()]);
    }

//    private static Suit[] complement(Suit a, Suit b)
//    {
//        int  index        = 0;
//        Suit compliment[] = new Suit[2];
//        for (Suit s : Suit.VALUES)
//        {
//            if (s != a && s != b)
//            {
//                compliment[ index++ ] = s;
//            }
//        }
//        return compliment;
//    }
//    private static Suit[] complement(Suit a, Suit b, Suit c)
//    {
//        for (Suit s : Suit.VALUES)
//        {
//            if (s != a && s != b && s != c)
//            {
//                return new Suit[]{ s };
//            }
//        }
//        throw new Error("all suits exhausted");
//    }
}
