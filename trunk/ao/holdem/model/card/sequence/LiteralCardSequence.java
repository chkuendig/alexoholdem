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

    public LiteralCardSequence(Hole      hole,
                               Community communitry)
    {
        HOLE      = hole;
        COMMUNITY = communitry;
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
    public String toString()
    {
        return HOLE + " | " + COMMUNITY;
    }
}
