package ao.holdem.v3.engine.state;

import ao.holdem.v3.model.Avatar;
import ao.holdem.v3.model.Stack;
import ao.holdem.v3.model.act.AbstractAction;
import ao.holdem.v3.model.act.Action;

/**
 *
 */
public class Seat
{
    //--------------------------------------------------------------------
    private final Avatar  player;
    private final Stack   commitment;
    private final boolean isAllIn;
    private final boolean isFolded;


    //--------------------------------------------------------------------
    public Seat(
            Avatar  playerHandle,
            Stack   totalCommitment,
            boolean allIn,
            boolean folded)
    {
        player     = playerHandle;
        commitment = totalCommitment;
        isAllIn    = allIn;
        isFolded   = folded;
    }


    //--------------------------------------------------------------------
    public Seat advance(
            Action action,
            Stack  stakes,
            Stack  betSize)
    {
        AbstractAction abstraction = action.abstraction();
        if (abstraction == AbstractAction.QUIT_FOLD) return fold();

        boolean nextIsAllIn = action.isAllIn();
        return abstraction == AbstractAction.CHECK_CALL
                ? call ( nextIsAllIn, stakes          )
                : raise( nextIsAllIn, stakes, betSize );
    }

    public Seat advanceBlind(Action act, Stack betSize)
    {
        return new Seat(
                    player, betSize, act.isAllIn(), false);
    }


    //--------------------------------------------------------------------
    public Seat fold()
    {
        return new Seat(
                player, commitment, isAllIn, true);
    }

    private Seat call(boolean nextIsAllIn, Stack stakes)
    {
        return new Seat(
                    player, stakes, nextIsAllIn, false);
    }

    private Seat raise(
            boolean nextIsAllIn, Stack stakes, Stack money)
    {
        return new Seat(player, stakes.plus(money),
                        nextIsAllIn, false);
    }

    public boolean isActive()
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

    public Stack commitment()
    {
        return commitment;
    }

    public Avatar player()
    {
        return player;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return player.toString();
    }
}
