package ao.holdem.model.card.chance;

import ao.holdem.model.Avatar;
import ao.holdem.model.Round;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.util.serial.Prototype;

/**
 *
 */
public interface ChanceCards
        extends Prototype<ChanceCards>
{
    public Community community(Round asOf);
    
    public Hole hole(Avatar forPlayer);
}
