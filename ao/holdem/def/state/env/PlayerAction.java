package ao.holdem.def.state.env;

/**
 *
 */
public class PlayerAction
{
    //--------------------------------------------------------------------
    private final Player PLAYER;
    private final TakenAction ACTION;


    //--------------------------------------------------------------------
    public PlayerAction(Player player, TakenAction action)
    {
        PLAYER = player;
        ACTION = action;
    }


    //--------------------------------------------------------------------
    public Player player()
    {
        return PLAYER;
    }

    public TakenAction action()
    {
        return ACTION;
    }
}
