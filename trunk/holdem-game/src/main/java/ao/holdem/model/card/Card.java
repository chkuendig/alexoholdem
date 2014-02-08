package ao.holdem.model.card;

import ao.holdem.persist.EnumBinding;
import ao.util.data.Arrs;

import java.util.Comparator;

/**
 * Note that this given order of cards
 *  is standard and will not be changed.
 */
@SuppressWarnings({"UnusedDeclaration"})
public enum Card
{
    //--------------------------------------------------------------------
    TWO_OF_CLUBS  (Rank.TWO,   Suit.CLUBS),
    THREE_OF_CLUBS(Rank.THREE, Suit.CLUBS),
    FOUR_OF_CLUBS (Rank.FOUR,  Suit.CLUBS),
    FIVE_OF_CLUBS (Rank.FIVE,  Suit.CLUBS),
    SIX_OF_CLUBS  (Rank.SIX,   Suit.CLUBS),
    SEVEN_OF_CLUBS(Rank.SEVEN, Suit.CLUBS),
    EIGHT_OF_CLUBS(Rank.EIGHT, Suit.CLUBS),
    NINE_OF_CLUBS (Rank.NINE,  Suit.CLUBS),
    TEN_OF_CLUBS  (Rank.TEN,   Suit.CLUBS),
    JACK_OF_CLUBS (Rank.JACK,  Suit.CLUBS),
    QUEEN_OF_CLUBS(Rank.QUEEN, Suit.CLUBS),
    KING_OF_CLUBS (Rank.KING,  Suit.CLUBS),
    ACE_OF_CLUBS  (Rank.ACE,   Suit.CLUBS),

    TWO_OF_DIAMONDS  (Rank.TWO,   Suit.DIAMONDS),
    THREE_OF_DIAMONDS(Rank.THREE, Suit.DIAMONDS),
    FOUR_OF_DIAMONDS (Rank.FOUR,  Suit.DIAMONDS),
    FIVE_OF_DIAMONDS (Rank.FIVE,  Suit.DIAMONDS),
    SIX_OF_DIAMONDS  (Rank.SIX,   Suit.DIAMONDS),
    SEVEN_OF_DIAMONDS(Rank.SEVEN, Suit.DIAMONDS),
    EIGHT_OF_DIAMONDS(Rank.EIGHT, Suit.DIAMONDS),
    NINE_OF_DIAMONDS (Rank.NINE,  Suit.DIAMONDS),
    TEN_OF_DIAMONDS  (Rank.TEN,   Suit.DIAMONDS),
    JACK_OF_DIAMONDS (Rank.JACK,  Suit.DIAMONDS),
    QUEEN_OF_DIAMONDS(Rank.QUEEN, Suit.DIAMONDS),
    KING_OF_DIAMONDS (Rank.KING,  Suit.DIAMONDS),
    ACE_OF_DIAMONDS  (Rank.ACE,   Suit.DIAMONDS),

    TWO_OF_HEARTS  (Rank.TWO,   Suit.HEARTS),
    THREE_OF_HEARTS(Rank.THREE, Suit.HEARTS),
    FOUR_OF_HEARTS (Rank.FOUR,  Suit.HEARTS),
    FIVE_OF_HEARTS (Rank.FIVE,  Suit.HEARTS),
    SIX_OF_HEARTS  (Rank.SIX,   Suit.HEARTS),
    SEVEN_OF_HEARTS(Rank.SEVEN, Suit.HEARTS),
    EIGHT_OF_HEARTS(Rank.EIGHT, Suit.HEARTS),
    NINE_OF_HEARTS (Rank.NINE,  Suit.HEARTS),
    TEN_OF_HEARTS  (Rank.TEN,   Suit.HEARTS),
    JACK_OF_HEARTS (Rank.JACK,  Suit.HEARTS),
    QUEEN_OF_HEARTS(Rank.QUEEN, Suit.HEARTS),
    KING_OF_HEARTS (Rank.KING,  Suit.HEARTS),
    ACE_OF_HEARTS  (Rank.ACE,   Suit.HEARTS),

    TWO_OF_SPADES  (Rank.TWO,   Suit.SPADES),
    THREE_OF_SPADES(Rank.THREE, Suit.SPADES),
    FOUR_OF_SPADES (Rank.FOUR,  Suit.SPADES),
    FIVE_OF_SPADES (Rank.FIVE,  Suit.SPADES),
    SIX_OF_SPADES  (Rank.SIX,   Suit.SPADES),
    SEVEN_OF_SPADES(Rank.SEVEN, Suit.SPADES),
    EIGHT_OF_SPADES(Rank.EIGHT, Suit.SPADES),
    NINE_OF_SPADES (Rank.NINE,  Suit.SPADES),
    TEN_OF_SPADES  (Rank.TEN,   Suit.SPADES),
    JACK_OF_SPADES (Rank.JACK,  Suit.SPADES),
    QUEEN_OF_SPADES(Rank.QUEEN, Suit.SPADES),
    KING_OF_SPADES (Rank.KING,  Suit.SPADES),
    ACE_OF_SPADES  (Rank.ACE,   Suit.SPADES);

    public static final Card VALUES[]  = Card.values(); // optimization
    public static final int  INDEXES[] = Arrs.sequence(VALUES.length);
    

    //--------------------------------------------------------------------
    /**
     * Create a new Card from the given rank and suit.
     * @param rank of card to create
     * @param suit of card to create
     * @return new Card with the given rank and suit.
     */
    public static Card valueOf(Rank rank, Suit suit)
    {
        return VALUES[ rank.ordinal() +
                       suit.ordinal() * Rank.values().length ];
    }
//    public static Card valueOf(int index)
//    {
//        return VALUES[ index - 1 ];
//    }

    // 5h Qd 6c Ac 6s
    public static Card valueOfCard(String name)
    {
        for (Card c : values())
        {
            if (c.toString().equals( name ))
            {
                return c;
            }
        }
        return null;
    }


    //--------------------------------------------------------------------
    private final Rank RANK;
    private final Suit SUIT;
    private final int  INDEX;
    private final int  INVERTED_INDEX;

    private Card(Rank rank, Suit suit)
    {
        RANK = rank;
        SUIT = suit;

        INDEX          = ordinal() + 1;
        INVERTED_INDEX = RANK.ordinal() * Suit.values().length +
                         SUIT.ordinal() + 1;
    }


    //--------------------------------------------------------------------
    public Rank rank()
    {
        return RANK;
    }

    public Suit suit()
    {
        return SUIT;
    }


    //--------------------------------------------------------------------
    /** Card to integer conversions:
        2c =  1    2d = 14    2h = 27    2s = 40
        3c =  2    3d = 15    3h = 28    3s = 41
        4c =  3    4d = 16    4h = 29    4s = 42
        5c =  4    5d = 17    5h = 30    5s = 43
        6c =  5    6d = 18    6h = 31    6s = 44
        7c =  6    7d = 19    7h = 32    7s = 45
        8c =  7    8d = 20    8h = 33    8s = 46
        9c =  8    9d = 21    9h = 34    9s = 47
        Tc =  9    Td = 22    Th = 35    Ts = 48
        Jc = 10    Jd = 23    Jh = 36    Js = 49
        Qc = 11    Qd = 24    Qh = 37    Qs = 50
        Kc = 12    Kd = 25    Kh = 38    Ks = 51
        Ac = 13    Ad = 26    Ah = 39    As = 52
     * @return the index from the table above
     */
    public int index()
    {
        return INDEX;
    }

    /** Card to inverted index conversion:
       2c =  1    2d =  2    2h =  3    2s =  4
       3c =  5    3d =  6    3h =  7    3s =  8
       4c =  9    4d = 10    4h = 11    4s = 12
       5c = 13    5d = 14    5h = 15    5s = 16
       6c = 17    6d = 18    6h = 19    6s = 20
       7c = 21    7d = 22    7h = 23    7s = 24
       8c = 25    8d = 26    8h = 27    8s = 28
       9c = 29    9d = 30    9h = 31    9s = 32
       Tc = 33    Td = 34    Th = 35    Ts = 36
       Jc = 37    Jd = 38    Jh = 39    Js = 40
       Qc = 41    Qd = 42    Qh = 43    Qs = 44
       Kc = 45    Kd = 46    Kh = 47    Ks = 48
       Ac = 49    Ad = 50    Ah = 51    As = 52
     * @return index from table above
     */
    public int invertedIndex()
    {
        return INVERTED_INDEX;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return RANK.toString() + SUIT;
    }


    //--------------------------------------------------------------------
    public static final EnumBinding<Card> BINDING =
                                new EnumBinding<Card>(Card.class);


    //--------------------------------------------------------------------
    public static final ByRank BY_RANK_DSC = new ByRank(false);
    public static final ByRank BY_RANK_ASC = new ByRank(true);
    public static class ByRank implements Comparator<Card>
    {
        private final boolean ascending;
        private ByRank(boolean asc)
        {
            ascending = asc;
        }

        public int compare(Card a, Card b)
        {
            int cmp = a.rank().compareTo( b.rank() );
            int cmpB =
                    cmp == 0
                    ? a.suit().compareTo( b.suit() )
                    : cmp;
            return ascending ? cmpB : -cmpB;
        }
        public Card min(Card a, Card b)
        {
            return compare(a, b) < 0 ? a : b;
        }
        public Card max(Card a, Card b)
        {
            return compare(a, b) > 0 ? a : b;
        }
    }
}
