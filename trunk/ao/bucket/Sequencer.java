package ao.bucket;

import ao.holdem.model.Round;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;

import java.util.EnumMap;

/**
 *
 */
public interface Sequencer
{
    public EnumMap<Round, Bucket>
            jointBucketSequence(Hole      hole,
                                Community community);
}
