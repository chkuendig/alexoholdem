package ao.holdem.v3.model.card.chance;

import ao.holdem.v3.model.Avatar;
import ao.holdem.v3.model.Round;
import ao.holdem.v3.model.card.Community;
import ao.holdem.v3.model.card.Hole;
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
