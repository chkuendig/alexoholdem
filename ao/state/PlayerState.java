package ao.state;

import ao.holdem.model.Money;
import ao.holdem.model.act.SimpleAction;
import ao.holdem.model.act.RealAction;
import ao.persist.PlayerHandle;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class PlayerState
{
    //--------------------------------------------------------------------
    private final boolean      isAllIn;
    private final boolean      isFolded;
    private final boolean      isUnacted;
    private final Money        commitment;
    private final PlayerHandle handle;


    //--------------------------------------------------------------------
    public PlayerState(
                     boolean      allIn,
                     boolean      folded,
                     boolean      unacted,
            @NotNull Money        totalCommitment,
            @NotNull PlayerHandle playerHandle)
    {
        isAllIn    = allIn;
        isFolded   = folded;
        isUnacted  = unacted;
        commitment = totalCommitment;
        handle     = playerHandle;
    }


    //--------------------------------------------------------------------
    public PlayerState advance(
            @NotNull RealAction action,
            @NotNull Money      stakes,
            @NotNull Money      betSize)
    {
        SimpleAction takenAction = action.toSimpleAction();
        if (takenAction == SimpleAction.FOLD) return fold();

        boolean nextIsAllIn = action.isAllIn();
        return takenAction == SimpleAction.CALL
                ? call ( nextIsAllIn, stakes          )
                : raise( nextIsAllIn, stakes, betSize );
    }

    public PlayerState advanceBlind(RealAction act, Money betSize)
    {
        return new PlayerState(
                    act.isAllIn(), false, true, betSize, handle);
    }


    //--------------------------------------------------------------------
    private PlayerState fold()
    {
        return new PlayerState(false, true, false, commitment, handle);
    }

    private PlayerState call(boolean nextIsAllIn, Money stakes)
    {
        return new PlayerState(
                    nextIsAllIn, false, false, stakes, handle);
    }

    private PlayerState raise(
            boolean nextIsAllIn, Money stakes, Money money)
    {
        return new PlayerState(nextIsAllIn, false, false,
                               stakes.plus(money), handle);
    }

    public boolean isIn()
    {
        return !(isAllIn() || isFolded());
    }


    //--------------------------------------------------------------------
    public boolean isAllIn()
    {
        return isAllIn;
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
