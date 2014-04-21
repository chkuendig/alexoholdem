package ao.holdem.engine.state;

import ao.holdem.model.ChipStack;
import ao.holdem.model.Round;
import ao.holdem.model.act.AbstractAction;
import ao.holdem.model.act.Action;

/**
 * State of position at a table.
 */
public class Seat
{
    //--------------------------------------------------------------------
    private final int       player;
    private final ChipStack commitment;
    private final boolean   isAllIn;
    private final boolean   isFolded;
    private final Round     lastVoluntaryActRound;


    //--------------------------------------------------------------------
    public Seat(int playerIndex)
    {
        this(playerIndex, ChipStack.ZERO, false, false, null);
    }

    private Seat(
            int       playerIndex,
            ChipStack totalCommitment,
            boolean   allIn,
            boolean   folded,
            Round     round)
    {
        player     = playerIndex;
        commitment = totalCommitment;
        isAllIn    = allIn;
        isFolded   = folded;

        lastVoluntaryActRound = round;
    }


    //--------------------------------------------------------------------
    public Seat advance(
            Action    action,
            ChipStack stakes,
            ChipStack betSize,
            Round     round)
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

    public Seat advanceBlind(Action act, ChipStack betSize)
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
                      ChipStack stakes,
                      Round   round)
    {
        return new Seat(
                    player, stakes, nextIsAllIn, false, round);
    }

    private Seat raise(
            boolean nextIsAllIn,
            ChipStack stakes,
            ChipStack money,
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
        return lastVoluntaryActRound != null;
    }

    public boolean voluntarilyActedDuring(Round round)
    {
        return lastVoluntaryActRound == round;
    }

    public ChipStack commitment()
    {
        return commitment;
    }

    public int player()
    {
        return player;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return String.valueOf(player);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Seat seat = (Seat) o;

        return isAllIn == seat.isAllIn &&
                isFolded == seat.isFolded &&
                !(commitment != null
                    ? !commitment.equals(seat.commitment)
                    : seat.commitment != null) &&
                lastVoluntaryActRound == seat.lastVoluntaryActRound;

    }

    @Override public int hashCode() {
        int result = player;
        result = 31 * result + (commitment != null ? commitment.hashCode() : 0);
        result = 31 * result + (isAllIn ? 1 : 0);
        result = 31 * result + (isFolded ? 1 : 0);
        result = 31 * result + (lastVoluntaryActRound != null
                                ? lastVoluntaryActRound.hashCode() : 0);
        return result;
    }
}
