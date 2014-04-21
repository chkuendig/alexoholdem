package ao.holdem.model.card.chance;

import ao.holdem.model.Round;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;

/**
 *
 */
public interface ChanceCards
{
    public Community community(Round asOf);
    
    public Hole hole(int forPlayer);
}
