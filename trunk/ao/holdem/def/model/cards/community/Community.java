package ao.holdem.def.model.cards.community;

/**
 * Immutable.
 */
public class Community
{
    //--------------------------------------------------------------------
    private final Flop FLOP;
    private final Turn TURN;
    private final River RIVER;

    //--------------------------------------------------------------------
    public Community()
    {
        this(null, null, null);
    }
    public Community(Flop flop)
    {
        this(flop, null, null);
    }

    public Community(Turn turn)
    {
        this(turn, turn, null);
    }

    public Community(River river)
    {
        this(river, river, river);
    }

    private Community(Flop flop, Turn turn, River river)
    {
        FLOP  = flop;
        TURN  = turn;
        RIVER = river;
    }


    //--------------------------------------------------------------------
    /**
     * @return null if not at flop yet
     */
    public Flop flop()
    {
        return FLOP;
    }


    //--------------------------------------------------------------------
    /**
     * @return null if not at turn yet
     */
    public Turn turn()
    {
        return TURN;
    }


    //--------------------------------------------------------------------
    /**
     * @return null if not at river yet
     */
    public River river()
    {
        return RIVER;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return RIVER != null
               ? RIVER.toString()
               : TURN != null
                 ? TURN.toString()
                 : FLOP != null
                   ? FLOP.toString()
                   : "none";
    }
}
