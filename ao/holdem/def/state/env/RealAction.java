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

    SMALL_BLIND,
    SMALL_BLIND_ALL_IN,
    BIG_BLIND,
    BIG_BLIND_ALL_IN,

    QUIT;


    //--------------------------------------------------------------------
    public boolean isAllIn()
    {
        return this == CALL_ALL_IN        ||
               this == RAISE_ALL_IN       ||
               this == BET_ALL_IN         ||
               this == SMALL_BLIND_ALL_IN ||
               this == BIG_BLIND_ALL_IN;
    }

    public RealAction toAllIn()
    {
        switch (this)
        {
            case CALL:        return CALL_ALL_IN;
            case RAISE:       return RAISE_ALL_IN;
            case BET:         return BET_ALL_IN;
            case SMALL_BLIND: return SMALL_BLIND_ALL_IN;
            case BIG_BLIND:   return BIG_BLIND_ALL_IN;
            default:          return null;
        }
    }

    public boolean isBlind()
    {
        return this == SMALL_BLIND        ||
               this == BIG_BLIND          ||
               this == SMALL_BLIND_ALL_IN ||
               this == BIG_BLIND_ALL_IN;
    }


    //--------------------------------------------------------------------
    public RealAction asSmallBlind()
    {
        assert isBlind();
        return this == BIG_BLIND
               ? SMALL_BLIND
               : this == BIG_BLIND_ALL_IN
                 ? SMALL_BLIND_ALL_IN
                 : this;
    }
    public RealAction asBigBlind()
    {
        assert isBlind();
        return this == SMALL_BLIND
               ? BIG_BLIND
               : this == SMALL_BLIND_ALL_IN
                 ? BIG_BLIND_ALL_IN
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
            case BET_ALL_IN:
            case RAISE:
            case RAISE_ALL_IN:
                return TakenAction.RAISE;

            case SMALL_BLIND:
            case SMALL_BLIND_ALL_IN:
            case BIG_BLIND:
            case BIG_BLIND_ALL_IN:
            default:
                return null;
        }
    }
}
