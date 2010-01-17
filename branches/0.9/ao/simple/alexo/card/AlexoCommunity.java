package ao.simple.alexo.card;

import ao.simple.alexo.state.AlexoRound;

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
    public AlexoCommunity truncate(AlexoRound asOf)
    {
        return new AlexoCommunity(
                (asOf == AlexoRound.PREFLOP
                 ? null : FLOP),
                (asOf == AlexoRound.TURN
                 ? TURN : null));
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
