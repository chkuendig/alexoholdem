package ao.holdem.model.card.chance;

import ao.holdem.model.Avatar;
import ao.holdem.model.Round;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;

import java.util.List;

/**
 *
 */
public class LiteralCards implements ChanceCards
{
    //--------------------------------------------------------------------
    private Community community;
    private List<Hole> holes;


    //--------------------------------------------------------------------
    public LiteralCards(Community community,
                        List<Hole> holesClockwiseDealerLast)
    {
        this.community = community;
        this.holes     = holesClockwiseDealerLast;
    }


    //--------------------------------------------------------------------
    public Community community(Round asOf)
    {
        return community.asOf( asOf );
    }


    //--------------------------------------------------------------------
    public Hole hole(int forPlayer)
    {
        return holes.get( forPlayer );
    }
}
