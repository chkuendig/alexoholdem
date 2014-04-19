package ao.holdem.model.card;

/**
 *
 */
public enum Rank
{
    //--------------------------------------------------------------------
    TWO("2"), THREE("3"), FOUR("4"),  FIVE("5"),
    SIX("6"), SEVEN("7"), EIGHT("8"), NINE("9"),
    TEN("T"), JACK("J"),  QUEEN("Q"), KING("K"),
    ACE("A");

    // pre-computed for performance reasons (should be immutable)
    public static final Rank VALUES[] = values();

    public static final Rank VALUES_REVERSE[];
    static
    {
        VALUES_REVERSE = new Rank[ VALUES.length ];
        for (int i = 0, j = VALUES.length - 1; j >= 0; i++, j--)
        {
            VALUES_REVERSE[ i ] = VALUES[ j ];
        }
    }


    //--------------------------------------------------------------------
    private final String name;


    //--------------------------------------------------------------------
    private Rank(String name)
    {
        this.name = name;
    }


    //--------------------------------------------------------------------
    public boolean comesAfter(Rank rank)
    {
        return ordinal() > rank.ordinal();
    }


    //--------------------------------------------------------------------
    @Override
    public String toString() {
        return name;
    }
}
