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
    private final String NAME;


    //--------------------------------------------------------------------
    private Rank(String name)
    {
        NAME  = name;
    }


    //--------------------------------------------------------------------
    public boolean comesAfter(Rank rank)
    {
        return ordinal() > rank.ordinal();
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return NAME;
    }
}
