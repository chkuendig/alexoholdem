package ao.simple.state;

/**
 * 
 */
public enum KuhnOutcome
{
    //--------------------------------------------------------------------
    SHOWDOWN(true),

    DOUBLE_SHOWDOWN(true),

    FIRST_TO_ACT_WINS(false),

    LAST_TO_ACT_WINS(false);


    //--------------------------------------------------------------------
    private final boolean IS_SHOWDOWN;


    //--------------------------------------------------------------------
    private KuhnOutcome(boolean isShowdown)
    {
        IS_SHOWDOWN = isShowdown;
    }


    //--------------------------------------------------------------------
    public boolean isShowdown()
    {
        return IS_SHOWDOWN;
    }
}
