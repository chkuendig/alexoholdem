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
    BET_ALL_IN,
    RAISE,
    RAISE_ALL_IN,

    BLIND, // resolved into SMALL_BLIND, or BIG_BLIND
           // so never occurs naturally
    BLIND_ALL_IN,
    SMALL_BLIND,
    SMALL_BLIND_ALL_IN,
    BIG_BLIND,
    BIG_BLIND_ALL_IN,

    QUIT;


    //--------------------------------------------------------------------
    public boolean isAllIn()
    {
        return this == CALL_ALL_IN  ||
               this == RAISE_ALL_IN ||
               this == BET_ALL_IN;
    }

    public RealAction toAllIn()
    {
        return (this == CALL)
                ? CALL_ALL_IN
                : (this == RAISE)
                   ? RAISE_ALL_IN
                   : (this == BET)
                      ? BET_ALL_IN
                      : this;
    }


    //--------------------------------------------------------------------
    public TakenAction toTakenAction()
    {
        switch (this)
        {
            case QUIT:
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
            default:
                return null;
        }
    }
}
