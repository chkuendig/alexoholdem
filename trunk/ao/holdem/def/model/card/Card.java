package ao.holdem.def.model.card;

import ao.holdem.def.model.card.eval5.Eval5Lookup;


/**
 * Ripped right out of
 *  http://www.suffecool.net/poker/evaluator.html
 */
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


    //--------------------------------------------------------------------
    public static Card valueOf(Rank rank, Suit suit)
    {
        return values()[ rank.ordinal() +
                         suit.ordinal() * Rank.values().length ];
    }


    //--------------------------------------------------------------------
    private final Rank RANK;
    private final Suit SUIT;
    private final int  SIGNITURE;
    private final int  INDEX;

    private Card(Rank rank, Suit suit)
    {
        RANK = rank;
        SUIT = suit;

//        +--------+--------+--------+--------+
//        |xxxbbbbb|bbbbbbbb|cdhsrrrr|xxpppppp|
//        +--------+--------+--------+--------+
//        p = prime number of rank (deuce=2,trey=3,four=5,...,ace=41)
//        r = rank of card (deuce=0,trey=1,four=2,five=3,...,ace=12)
//        cdhs = suit of card (bit turned on based on suit of card)
//        b = bit turned on depending on rank of card
        SIGNITURE = RANK.MASK      << 16 |
                    SUIT.mask()    << 12 |
                    RANK.ordinal() << 8  |
                    RANK.PRIME;

        INDEX = ordinal() + 1;
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

    /* Card to integer conversions:
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

        Card.index()
	*/
    public int index()
    {
        return INDEX;
    }


    //--------------------------------------------------------------------
    // higher means better (unlike in original algorithm)
    public static short handValue(Card... fiveCards)
    {
        assert fiveCards.length == 5;
        return handValue(fiveCards[0], fiveCards[1], fiveCards[2],
                        fiveCards[3], fiveCards[4]);
    }
    public static short handValue(
            Card c1, Card c2, Card c3, Card c4, Card c5)
    {
        return handValue(c1.SIGNITURE, c2.SIGNITURE, c3.SIGNITURE,
                         c4.SIGNITURE, c5.SIGNITURE);
    }
    public static short handValue(int c1, int c2, int c3, int c4, int c5)
    {
        return (short)(7462 - inverseValue(c1, c2, c3, c4, c5));
    }
    public static short inverseValue(int c1, int c2, int c3, int c4, int c5)
    {
        int index = (c1 | c2 | c3 | c4 | c5) >> 16;

        // check for flushes and straight flushes
        if (suitsEqual(c1, c2, c3, c4, c5))
        {
            return Eval5Lookup.flushes( index );
        }

        // check for straights and high card hands
        short high = Eval5Lookup.unique5( index );
        if (high != 0) return high;

        return Eval5Lookup.remainingHands(
                (c1 & 0xff) * (c2 & 0xff) * (c3 & 0xff) *
                (c4 & 0xff) * (c5 & 0xff));
    }


    public static boolean suitsEqual(
            Card c1, Card c2, Card c3, Card c4, Card c5)
    {
        return suitsEqual(c1.SIGNITURE, c2.SIGNITURE, c3.SIGNITURE,
                          c4.SIGNITURE, c5.SIGNITURE);
    }
    private static boolean suitsEqual(
            int c1, int c2, int c3, int c4, int c5)
    {
        return (c1 & c2 & c3 & c4 & c5 & 0xf000) > 0;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return RANK.toString() + SUIT;
    }


    //--------------------------------------------------------------------
    public enum Rank
    {
        TWO(2, "2"),   THREE(3, "3"),  FOUR(5, "4"),   FIVE(7, "5"),
        SIX(11, "6"),  SEVEN(13, "7"), EIGHT(17, "8"), NINE(19, "9"),
        TEN(23, "10"), JACK(29, "J"),  QUEEN(31, "Q"), KING(37, "K"),
        ACE(41, "A");

        private final int    PRIME;
        private final int    MASK;
        private final String NAME;

        private Rank(int prime, String name)
        {
            PRIME = prime;
            MASK  = 1 << ordinal();
            NAME  = name;
        }

        public String toString()
        {
            return NAME;
        }
    }

    
    //--------------------------------------------------------------------
    public enum Suit
    {
        CLUBS("c"), DIAMONDS("d"), HEARTS("h"), SPADES("s");

        private final String NAME;
        private Suit(String name)
        {
            NAME = name;
        }

        private int mask()
        {
            return 1 << (3 - ordinal());
        }

        public String toString()
        {
            return NAME;
        }
    }
}
