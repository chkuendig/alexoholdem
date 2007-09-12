package ao.holdem.def.state.env;

/**
 *
 */
public enum RealAction
{
    //--------------------------------------------------------------------
    FOLD,

    CHECK,
    CALL,
    CALL_ALL_IN,

    BET,
    RAISE,
    RAISE_ALL_IN,

    SMALL_BLIND,
    BIG_BLIND,

    QUIT;

    
    //--------------------------------------------------------------------
    public TakenAction toTakenAction()
    {
        switch (this)
        {
            case FOLD:
                return TakenAction.FOLD;

            case CHECK:
            case CALL:
            case CALL_ALL_IN:
                return TakenAction.CALL;

            case BET:
            case RAISE:
            case RAISE_ALL_IN:
                return TakenAction.RAISE;

            case SMALL_BLIND:
            case BIG_BLIND:
            case QUIT:
            default:
                return null;
        }
    }
}
