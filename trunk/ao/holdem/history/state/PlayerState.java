package ao.holdem.history.state;

import ao.holdem.def.model.Money;
import ao.holdem.history.PlayerHandle;

/**
 *
 */
public class PlayerState
{
    //--------------------------------------------------------------------
    private final boolean      isActive;
    //private final boolean      isSmallBlind;
    //private final boolean      isBigBlind;
    private final boolean      isFolded;
    private final boolean      isUnacted;
    private final Money        commitment;
    private final PlayerHandle handle;


    //--------------------------------------------------------------------
    public PlayerState(
            boolean      active,
            boolean      folded,
            boolean      unacted,
            //boolean      smallBlind,
            //boolean      bigBlind,
            Money        totalCommitment,
            PlayerHandle playerHandle)
    {
        isActive     = active;
        isFolded     = folded;
        isUnacted    = unacted;
        //isSmallBlind = smallBlind;
        //isBigBlind   = bigBlind;
        commitment   = totalCommitment;
        handle       = playerHandle;
    }


    //--------------------------------------------------------------------
    public boolean isActive()
    {
        return isActive;
    }

    public boolean isFolded()
    {
        return isFolded;
    }

    public boolean isUnacted()
    {
        return isUnacted;
    }

//    public boolean isSmallBlind()
//    {
//        return isSmallBlind;
//    }
//
//    public boolean isBigBlind()
//    {
//        return isBigBlind;
//    }

    public Money commitment()
    {
        return commitment;
    }

    public PlayerHandle handle()
    {
        return handle;
    }
}
