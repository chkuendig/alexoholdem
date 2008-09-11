package ao.holdem.model.card;

/**
 * important:
 *  the order of the cards [clubs, diamonds, hearts, spades] is
 *  chritical to the operations of this program.
 */
public enum Suit
{
    //--------------------------------------------------------------------
    CLUBS("c"), DIAMONDS("d"), HEARTS("h"), SPADES("s");

    public static Suit VALUES[] = values();


    //--------------------------------------------------------------------
    private final String NAME;


    //--------------------------------------------------------------------
    private Suit(String name)
    {
        NAME = name;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return NAME;
    }
}
