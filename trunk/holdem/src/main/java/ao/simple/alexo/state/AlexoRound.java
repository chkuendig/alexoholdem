package ao.simple.alexo.state;

/**
 *
 */
public enum AlexoRound
{
    //--------------------------------------------------------------------
    TURN(null),
    FLOP(TURN),
    PREFLOP(FLOP);

    public static final AlexoRound[] VALUES = values();


    //--------------------------------------------------------------------
    private final AlexoRound NEXT;


    //--------------------------------------------------------------------
    private AlexoRound(AlexoRound next)
    {
        NEXT = next;
    }

    //--------------------------------------------------------------------
    public AlexoRound next()
    {
        return NEXT;
    }
}
