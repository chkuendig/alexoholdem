package ao.holdem.model.card;

/**
 * important:
 *  the order of the cards [clubs, diamonds, hearts, spades] is
 *  critical to the operations of this program.
 */
public enum Suit
{
    //--------------------------------------------------------------------
    CLUBS("c"), DIAMONDS("d"), HEARTS("h"), SPADES("s");

    // pre-computed for performance reasons (should be immutable)
    public static final Suit VALUES[] = values();


    //--------------------------------------------------------------------
    private final String name;


    //--------------------------------------------------------------------
    private Suit(String name)
    {
        this.name = name;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return name;
    }
}
