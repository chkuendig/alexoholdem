package ao.holdem.model.card;

import ao.bucket.index.hole.CanonHole;
import ao.bucket.index.hole.HoleLookup;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;


/**
 * Hole cards, with canonical indexing
 *          (accounting for suit isomorphisms).
 */
public class Hole
{
    //--------------------------------------------------------------------
    private static final Hole[][] VALUES  = new Hole[52][52];
    static
    {
        Card cards[] = Card.VALUES;
        for (int i = 0; i < 52; i++)
        {
            for (int j = 0; j < 52; j++)
            {
                if (i == j) continue;
                Hole hole    = new Hole(cards[i], cards[j]);
                VALUES[i][j] = hole;
            }
        }
    }


    //--------------------------------------------------------------------
    public static Hole valueOf(Card a, Card b)
    {
        return VALUES[ a.ordinal() ][ b.ordinal() ];
    }


    //--------------------------------------------------------------------
    private final Card      A, B;
    private final Card      AS_ARRAY[];
    private final boolean   IS_PAIR;


    //--------------------------------------------------------------------
    private Hole(Card a, Card b)
    {
        assert a != null && b != null;
        assert a != b;

        A           = a;
        B           = b;
        AS_ARRAY    = new Card[]{A, B};
        IS_PAIR     = computePaired();
    }


    //--------------------------------------------------------------------
    public Card a()
    {
        return A;
    }

    public Card b()
    {
        return B;
    }

    public CanonHole asCanon()
    {
        return HoleLookup.lookup(A, B);
    }


    //--------------------------------------------------------------------
    public boolean ranks(Rank rankA, Rank rankB)
    {
        return A.rank() == rankA && B.rank() == rankB ||
               A.rank() == rankB && B.rank() == rankA;
    }

    public boolean ranks(Rank rank)
    {
        return A.rank() == rank || B.rank() == rank;
    }

    public boolean suited()
    {
        return A.suit() == B.suit();
    }

    public boolean paired()
    {
        return IS_PAIR;
    }
    private boolean computePaired()
    {
        return A.rank() == B.rank();
    }

    public boolean hasXcard()
    {
        return A.rank().ordinal() < Rank.JACK.ordinal() ||
                B.rank().ordinal() < Rank.JACK.ordinal();
    }

    public boolean contains(Card card)
    {
        return A == card || B == card;
    }


    //--------------------------------------------------------------------
    public Card hi()
    {
        assert !paired();
        return (A.rank().compareTo( B.rank() ) > 0
                ? A : B);
    }
    public Card lo()
    {
        assert !paired();
        return (A.rank().compareTo( B.rank() ) < 0
                ? A : B);
    }

    public Card[] asArray()
    {
        return AS_ARRAY;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return "[" + A + ", " + B + "]";
    }

    @SuppressWarnings({"EqualsWhichDoesntCheckParameterClass"})
    public boolean equals(Object obj)
    {
        return this == obj;
    }

    public int hashCode()
    {
        return A.ordinal() * 52 + B.ordinal();
    }


    //--------------------------------------------------------------------
    public static final Binding BINDING = new Binding();
    public static class Binding extends TupleBinding
    {
        public Hole entryToObject(TupleInput input)
        {
            byte cardA = input.readByte();
            byte cardB = input.readByte();
            return Hole.VALUES[ cardA ][ cardB ];
        }

        public void objectToEntry(Object object, TupleOutput output)
        {
            Hole hole = (Hole) object;

            if (hole == null)
            {
                output.writeByte( (byte) 0                );
                output.writeByte( (byte) 0                );
            }
            else
            {
                output.writeByte( (byte) hole.A.ordinal() );
                output.writeByte( (byte) hole.B.ordinal() );
            }
        }
    }
}
