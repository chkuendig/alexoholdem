package ao.holdem.history.state;

import ao.holdem.def.model.Money;
import ao.holdem.def.state.env.RealAction;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.PlayerHandle;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class PlayerState
{
    //--------------------------------------------------------------------
    private final boolean      isActive;
    private final boolean      isFolded;
    private final boolean      isUnacted;
    private final Money        commitment;
    private final PlayerHandle handle;


    //--------------------------------------------------------------------
    public PlayerState(
                     boolean      active,
                     boolean      folded,
                     boolean      unacted,
            @NotNull Money        totalCommitment,
            @NotNull PlayerHandle playerHandle)
    {
        isActive     = active;
        isFolded     = folded;
        isUnacted    = unacted;
        commitment   = totalCommitment;
        handle       = playerHandle;
    }


    //--------------------------------------------------------------------
    public PlayerState advance(
            @NotNull RealAction  action,
            @NotNull PlayerState prevActive,
            @NotNull Money       betSize)
    {
        TakenAction takenAction = action.toTakenAction();
        if (takenAction == TakenAction.FOLD) return fold();

        boolean nextIsActive = !action.isAllIn();
        return takenAction == TakenAction.CALL
                ? call ( nextIsActive, prevActive          )
                : raise( nextIsActive, prevActive, betSize );
    }


    //--------------------------------------------------------------------
    private PlayerState fold()
    {
        return new PlayerState(false, true, false, commitment, handle);
    }

    private PlayerState call(boolean nextIsActive, PlayerState of)
    {
        return new PlayerState(
                    nextIsActive, false, false, of.commitment, handle);
    }

    private PlayerState raise(
            boolean nextIsActive, PlayerState player, Money money)
    {
        return new PlayerState(nextIsActive, false, false,
                               player.commitment.plus(money), handle);
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

    public Money commitment()
    {
        return commitment;
    }

    public PlayerHandle handle()
    {
        return handle;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return handle.toString();
    }
}
