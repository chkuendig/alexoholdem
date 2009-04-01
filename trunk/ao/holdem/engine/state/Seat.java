package ao.holdem.engine.state;

import ao.holdem.model.Avatar;
import ao.holdem.model.Chips;
import ao.holdem.model.Round;
import ao.holdem.model.act.AbstractAction;
import ao.holdem.model.act.Action;

/**
 *
 */
public class Seat
{
    //--------------------------------------------------------------------
    private final Avatar  player;
    private final Chips   commitment;
    private final boolean isAllIn;
    private final boolean isFolded;
    private final Round   lastVolanteryActRound;


    //--------------------------------------------------------------------
    public Seat(Avatar playerHandle)
    {
        this(playerHandle, Chips.ZERO, false, false, null);
    }

    private Seat(
            Avatar  playerHandle,
            Chips   totalCommitment,
            boolean allIn,
            boolean folded,
            Round   round)
    {
        player     = playerHandle;
        commitment = totalCommitment;
        isAllIn    = allIn;
        isFolded   = folded;

        lastVolanteryActRound = round;
    }


    //--------------------------------------------------------------------
    public Seat advance(
            Action action,
            Chips  stakes,
            Chips  betSize,
            Round  round)
    {
        AbstractAction abstraction = action.abstraction();
        if (abstraction == AbstractAction.QUIT_FOLD)
        {
            return fold(round);
        }

        boolean nextIsAllIn = action.isAllIn();
        return abstraction == AbstractAction.CHECK_CALL
                ? call ( nextIsAllIn, stakes         , round)
                : raise( nextIsAllIn, stakes, betSize, round );
    }

    public Seat advanceBlind(Action act, Chips betSize)
    {
        return new Seat(
                    player, betSize, act.isAllIn(), false, null);
    }


    //--------------------------------------------------------------------
    public Seat fold(Round round)
    {
        return new Seat(
                player, commitment, isAllIn, true, round);
    }

    private Seat call(boolean nextIsAllIn,
                      Chips   stakes,
                      Round   round)
    {
        return new Seat(
                    player, stakes, nextIsAllIn, false, round);
    }

    private Seat raise(
            boolean nextIsAllIn,
            Chips   stakes,
            Chips   money,
            Round   round)
    {
        return new Seat(player, stakes.plus(money),
                        nextIsAllIn, false, round);
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

    public boolean hasActedVoluntarily()
    {
        return lastVolanteryActRound != null;
    }

    public boolean voluntarilyActedDuring(Round round)
    {
        return lastVolanteryActRound == round;
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
    @Override public String toString()
    {
        return player.toString();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Seat seat = (Seat) o;

        return isAllIn == seat.isAllIn &&
                isFolded == seat.isFolded &&
                !(commitment != null
                    ? !commitment.equals(seat.commitment)
                    : seat.commitment != null) &&
                lastVolanteryActRound == seat.lastVolanteryActRound;

    }

    @Override public int hashCode() {
        int result = player != null ? player.hashCode() : 0;
        result = 31 * result + (commitment != null ? commitment.hashCode() : 0);
        result = 31 * result + (isAllIn ? 1 : 0);
        result = 31 * result + (isFolded ? 1 : 0);
        result = 31 * result + (lastVolanteryActRound != null
                                ? lastVolanteryActRound.hashCode() : 0);
        return result;
    }
}
