package ao.holdem.engine.state;

import ao.holdem.model.Avatar;
import ao.holdem.model.Chips;
import ao.holdem.model.act.AbstractAction;
import ao.holdem.model.act.Action;

/**
 *
 */
public class Seat
{
    //--------------------------------------------------------------------
    private final Avatar  player;
    private final Chips commitment;
    private final boolean isAllIn;
    private final boolean isFolded;


    //--------------------------------------------------------------------
    public Seat(
            Avatar  playerHandle,
            Chips totalCommitment,
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
            Chips stakes,
            Chips betSize)
    {
        AbstractAction abstraction = action.abstraction();
        if (abstraction == AbstractAction.QUIT_FOLD) return fold();

        boolean nextIsAllIn = action.isAllIn();
        return abstraction == AbstractAction.CHECK_CALL
                ? call ( nextIsAllIn, stakes          )
                : raise( nextIsAllIn, stakes, betSize );
    }

    public Seat advanceBlind(Action act, Chips betSize)
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

    private Seat call(boolean nextIsAllIn, Chips stakes)
    {
        return new Seat(
                    player, stakes, nextIsAllIn, false);
    }

    private Seat raise(
            boolean nextIsAllIn, Chips stakes, Chips money)
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

    public Chips commitment()
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
