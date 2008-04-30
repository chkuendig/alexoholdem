package ao.holdem.model.card.sequence;

import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;

/**
 *
 */
public interface CardSequence
{
    /**
     * @return hole cards corresponding to this player.
     */
    public Hole      hole();


    /**
     * @return shared community cards for this stage of the hand.
     */
    public Community community();
}
