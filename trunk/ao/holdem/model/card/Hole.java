package ao.holdem.model.card;

import ao.bucket.index.iso_cards.Ordering;
import ao.bucket.index.iso_flop.IsoFlop;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;


/**
 * Hole cards.
 *
 * Both cards are defined (not null).
 */
public class Hole
{
    //--------------------------------------------------------------------
    private static final Hole[][] VALUES = new Hole[52][52];
    static
    {
        Card cards[] = Card.VALUES;
        for (int i = 0; i < 52; i++)
        {
            for (int j = 0; j < 52; j++)
            {
                if (i == j) continue;
                VALUES[i][j] = new Hole(cards[i], cards[j]);
            }
        }
    }

    private static final int PAIR_COUNT             = 13;
    private static final int PAIR_PLUS_SUITED_COUNT = PAIR_COUNT + 78;
    public  static final int SUIT_ISOMORPHIC_COUNT  = 169;


    //--------------------------------------------------------------------
    public static Hole valueOf(Card a, Card b)
    {
        return VALUES[ a.ordinal() ][ b.ordinal() ];
    }


    //--------------------------------------------------------------------
    private final Card     A;
    private final Card     B;
    private final int      ISO_INDEX;
    private final Ordering ORDERING;


    //--------------------------------------------------------------------
    private Hole(Card a, Card b)
    {
        assert a != null && b != null;
        assert a != b;

        A         = a;
        B         = b;
        ISO_INDEX = computeSuitIsomorphicIndex();
        ORDERING  = computeOrdering();
    }


    //--------------------------------------------------------------------
    private int computeSuitIsomorphicIndex()
    {
        if (paired())
        {
            return A.rank().ordinal();
        }
        else
        {
            int hi  = hi().rank().ordinal();
            int lo  = lo().rank().ordinal();

            int subIndex = hi * (hi - 1) / 2 + lo;

            return (suited()
                    ? PAIR_COUNT
                    : PAIR_PLUS_SUITED_COUNT) +
                   subIndex;
        }
    }

    private Ordering computeOrdering()
    {
        return paired()
                ? Ordering.pair(A.suit(), B.suit())
                : suited()
                  ? Ordering.suited  (A.suit())
                  : Ordering.unsuited(hi().suit(), lo().suit());
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

    public int suitIsomorphicIndex()
    {
        return ISO_INDEX;
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
        return new Card[]{A, B};
    }
    

    //--------------------------------------------------------------------
    public IsoFlop isoFlop(Card... flop)
    {
        return new IsoFlop(ORDERING, asArray(), flop);
    }

    public Ordering ordering()
    {
        return ORDERING;
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
