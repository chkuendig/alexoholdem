package ao.holdem.model.card.sequence;

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
    public String toString()
    {
        return HOLE + " | " + COMMUNITY;
    }
}
