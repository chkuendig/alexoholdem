package ao.holdem.model.card;


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
        Card[] cards = Card.VALUES;
        for (int i = 0; i < 52; i++)
        {
            for (int j = 0; j < 52; j++)
            {
                if (i == j) continue;

                Hole hole = new Hole(cards[i], cards[j]);
                VALUES[i][j] = hole;
            }
        }
    }


    //------------------------------------------------------------------------
    /**
     * Create a new instance of a Hole.
     * @param a the first card
     * @param b the second card
     * @return Hole consisting of a and b, if a == b then null.
     * @throws NullPointerException if a or b are null
     */
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
        IS_PAIR     = computeIsPair();
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

//    public CanonHole asCanon()
//    {
//        return CanonHole.create(A, B);
//    }


    //--------------------------------------------------------------------
    public boolean hasRanks(Rank rankA, Rank rankB)
    {
        return A.rank() == rankA && B.rank() == rankB ||
               A.rank() == rankB && B.rank() == rankA;
    }

    public boolean hasRank(Rank rank)
    {
        return A.rank() == rank || B.rank() == rank;
    }

    public boolean isSuited()
    {
        return A.suit() == B.suit();
    }

    public boolean isPair()
    {
        return IS_PAIR;
    }
    private boolean computeIsPair()
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
    /**
     * @return the card with the higher rank.
     * @throws AssertionError if Hole is paired()
     */
    public Card high()
    {
        assert !isPair();
        return (A.rank().compareTo( B.rank() ) > 0
                ? A : B);
    }

    /**
     * @return the card with the lower rank.
     * @throws AssertionError if Hole is paired()
     */
    public Card low()
    {
        assert !isPair();
        return (A.rank().compareTo( B.rank() ) < 0
                ? A : B);
    }

    public Card[] asArray()
    {
        return AS_ARRAY;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "[" + A + ", " + B + "]";
    }

    @Override
    @SuppressWarnings({"EqualsWhichDoesntCheckParameterClass"})
    public boolean equals(Object obj)
    {
        return this == obj;
    }

    @Override
    public int hashCode()
    {
        return A.ordinal() * 52 + B.ordinal();
    }
}
