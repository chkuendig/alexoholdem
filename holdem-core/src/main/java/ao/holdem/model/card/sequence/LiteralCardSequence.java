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
    private final Hole hole;
    private final Community community;


    //--------------------------------------------------------------------
    public LiteralCardSequence(Hole hole)
    {
        this(hole, Community.PREFLOP);
    }

    public LiteralCardSequence(
            Hole      hole,
            Community community)
    {
        this.hole = hole;
        this.community = community;
    }


    //--------------------------------------------------------------------
    public Hole hole()
    {
        return hole;
    }

    public Community community()
    {
        return community;
    }


    //--------------------------------------------------------------------
    public LiteralCardSequence addFlop(
            Card flopA, Card flopB, Card flopC)
    {
        return new LiteralCardSequence(
                hole,
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
                hole,
                community.addTurn(turn));
    }

    public LiteralCardSequence addRiver(
            Card river)
    {
        return new LiteralCardSequence(
                hole,
                community.addRiver(river));
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return hole + " | " + community;
    }


    //--------------------------------------------------------------------
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LiteralCardSequence that = (LiteralCardSequence) o;
        return !(community != null
                ? !community.equals(that.community)
                : that.community != null) &&
               !(hole != null
                 ? !hole.equals(that.hole) : that.hole != null);
    }

    @Override public int hashCode() {
        int result = hole != null ? hole.hashCode() : 0;
        result = 31 * result +
                (community != null ? community.hashCode() : 0);
        return result;
    }
}
