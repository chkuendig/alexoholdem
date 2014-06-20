package ao.holdem.model.card;


import java.util.EnumSet;
import java.util.Set;

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
        if (a == b) {
            throw new IllegalArgumentException("Hole cards must be unique");
        }
        return VALUES[ a.ordinal() ][ b.ordinal() ];
    }


    //--------------------------------------------------------------------
    private final Card a, b;
    private final boolean isPair;


    //--------------------------------------------------------------------
    private Hole(Card a, Card b)
    {
        assert a != null && b != null;
        assert a != b;

        this.a = a;
        this.b = b;

        isPair = computeIsPair();
    }


    //--------------------------------------------------------------------
    public Card a()
    {
        return a;
    }

    public Card b()
    {
        return b;
    }

//    public CanonHole asCanon()
//    {
//        return CanonHole.create(a, b);
//    }


    //--------------------------------------------------------------------
    public boolean hasRanks(Rank rankA, Rank rankB)
    {
        return a.rank() == rankA && b.rank() == rankB ||
               a.rank() == rankB && b.rank() == rankA;
    }

    public boolean hasRank(Rank rank)
    {
        return a.rank() == rank || b.rank() == rank;
    }

    public boolean isSuited()
    {
        return a.suit() == b.suit();
    }

    public boolean isPair()
    {
        return isPair;
    }
    private boolean computeIsPair()
    {
        return a.rank() == b.rank();
    }

    public boolean hasXcard()
    {
        return a.rank().ordinal() < Rank.JACK.ordinal() ||
                b.rank().ordinal() < Rank.JACK.ordinal();
    }

    public boolean contains(Card card)
    {
        return a == card || b == card;
    }


    //--------------------------------------------------------------------
    /**
     * @return the card with the higher rank.
     * @throws AssertionError if Hole is paired()
     */
    public Card high()
    {
        assert !isPair();
        return (a.rank().compareTo( b.rank() ) > 0
                ? a : b);
    }

    /**
     * @return the card with the lower rank.
     * @throws AssertionError if Hole is paired()
     */
    public Card low()
    {
        assert !isPair();
        return (a.rank().compareTo( b.rank() ) < 0
                ? a : b);
    }


    public Set<Card> toSet() {
        return EnumSet.of(a, b);
    }
    public Card[] toArray() {
        return new Card[]{a, b};
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "[" + a + ", " + b + "]";
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
        return a.ordinal() * 52 + b.ordinal();
    }
}
