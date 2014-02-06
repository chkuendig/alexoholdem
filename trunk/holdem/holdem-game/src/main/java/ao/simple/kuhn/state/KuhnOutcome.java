package ao.simple.kuhn.state;

/**
 * 
 */
public enum KuhnOutcome
{
    //--------------------------------------------------------------------
    SHOWDOWN       (true),
    DOUBLE_SHOWDOWN(true),
    PLAYER_ONE_WINS(false),
    PLAYER_TWO_WINS(false);


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
