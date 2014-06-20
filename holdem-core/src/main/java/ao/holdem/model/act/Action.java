package ao.holdem.model.act;


/**
 * An action in Limit Holdem.
 */
public enum Action
{
    //--------------------------------------------------------------------
    QUIT(false, false, AbstractAction.QUIT_FOLD),
    FOLD(false, false, AbstractAction.QUIT_FOLD),

    CHECK(       false, false, AbstractAction.CHECK_CALL),
    CALL(        false, false, AbstractAction.CHECK_CALL),
    CALL_ALL_IN( true,  false, AbstractAction.CHECK_CALL),

    BET(          false, false, AbstractAction.BET_RAISE),
    BET_ALL_IN(   true,  false, AbstractAction.BET_RAISE),
    RAISE(        false, false, AbstractAction.BET_RAISE),
    RAISE_ALL_IN( true,  false, AbstractAction.BET_RAISE),

    SMALL_BLIND(        false, true, null),
    SMALL_BLIND_ALL_IN( true,  true, null),
    BIG_BLIND(          false, true, null),
    BIG_BLIND_ALL_IN(   true,  true, null);

    public static final Action[] VALUES = values();


    //--------------------------------------------------------------------
    private final boolean        IS_ALL_IN;
    private final boolean        IS_BLIND;
    private final AbstractAction ABSTRACTION;


    //--------------------------------------------------------------------
    private Action(boolean        isAllIn,
                   boolean        isBlind,
                   AbstractAction abstraction)
    {
        IS_ALL_IN   = isAllIn;
        IS_BLIND    = isBlind;
        ABSTRACTION = abstraction;
    }


    //--------------------------------------------------------------------
    public boolean isAllIn()
    {
        return IS_ALL_IN;
    }

    public Action toAllIn()
    {
        switch (this)
        {
            case CALL:        return CALL_ALL_IN;
            case RAISE:       return RAISE_ALL_IN;
            case BET:         return BET_ALL_IN;
            case SMALL_BLIND: return SMALL_BLIND_ALL_IN;
            case BIG_BLIND:   return BIG_BLIND_ALL_IN;
        }
        throw new Error("can't convert " + this + " to all-in");
    }



    //--------------------------------------------------------------------
    public boolean isBlind()
    {
        return IS_BLIND;
    }
    public boolean isSmallBlind()
    {
        return this == SMALL_BLIND        ||
               this == SMALL_BLIND_ALL_IN;
    }

    public Action asSmallBlind()
    {
        assert isBlind();
        return this == BIG_BLIND
               ? SMALL_BLIND
               : this == BIG_BLIND_ALL_IN
                 ? SMALL_BLIND_ALL_IN
                 : this;
    }
    public Action asBigBlind()
    {
        assert isBlind();
        return this == SMALL_BLIND
               ? BIG_BLIND
               : this == SMALL_BLIND_ALL_IN
                 ? BIG_BLIND_ALL_IN
                 : this;
    }


    //--------------------------------------------------------------------
    public AbstractAction abstraction()
    {
        return ABSTRACTION;
    }

    public boolean is(AbstractAction abstraction)
    {
        return ABSTRACTION == abstraction;
    }
}
