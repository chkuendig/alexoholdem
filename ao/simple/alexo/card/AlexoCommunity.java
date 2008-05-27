package ao.simple.alexo.card;

/**
 *
 */
public class AlexoCommunity
{
    //--------------------------------------------------------------------
    private final AlexoCard FLOP;
    private final AlexoCard TURN;


    //--------------------------------------------------------------------
    public AlexoCommunity(AlexoCard flop,
                          AlexoCard turn)
    {
        FLOP = flop;
        TURN = turn;
    }


    //--------------------------------------------------------------------
    public AlexoCard flop()
    {
        return FLOP;
    }

    public AlexoCard turn()
    {
        return TURN;
    }
}
