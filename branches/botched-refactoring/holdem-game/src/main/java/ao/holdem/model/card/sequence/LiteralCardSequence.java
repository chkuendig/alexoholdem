package ao.holdem.model.card.sequence;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;

/**
 *
 */
public class LiteralCardSequence implements CardSequence
{
    //--------------------------------------------------------------------
    private final Hole      HOLE;
    private final Community COMMUNITY;


    //--------------------------------------------------------------------
    public LiteralCardSequence(Hole hole)
    {
        this(hole, Community.PREFLOP);
    }

    public LiteralCardSequence(
            Hole      hole,
            Community community)
    {
        HOLE      = hole;
        COMMUNITY = community;
    }


    //--------------------------------------------------------------------
    public Hole hole()
    {
        return HOLE;
    }

    public Community community()
    {
        return COMMUNITY;
    }


    //--------------------------------------------------------------------
    public LiteralCardSequence addFlop(
            Card flopA, Card flopB, Card flopC)
    {
        return new LiteralCardSequence(
                HOLE,
                new Community(flopA, flopB, flopC));
    }
    public LiteralCardSequence addFlop(
            Card flop[])
    {
        return addFlop(flop[0], flop[1], flop[2]);
    }

    public LiteralCardSequence addTurn(
            Card turn)
    {
        return new LiteralCardSequence(
                HOLE,
                COMMUNITY.addTurn(turn));
    }

    public LiteralCardSequence addRiver(
            Card river)
    {
        return new LiteralCardSequence(
                HOLE,
                COMMUNITY.addRiver(river));
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return HOLE + " | " + COMMUNITY;
    }


    //--------------------------------------------------------------------
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LiteralCardSequence that = (LiteralCardSequence) o;
        return !(COMMUNITY != null
                ? !COMMUNITY.equals(that.COMMUNITY)
                : that.COMMUNITY != null) &&
               !(HOLE != null
                 ? !HOLE.equals(that.HOLE) : that.HOLE != null);
    }

    @Override public int hashCode() {
        int result = HOLE != null ? HOLE.hashCode() : 0;
        result = 31 * result +
                (COMMUNITY != null ? COMMUNITY.hashCode() : 0);
        return result;
    }
}
