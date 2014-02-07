package ao.holdem.ai.ai.gen;

import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 *
 */
public class ChanceSequence
{
    private final ImmutableList<Hole> clockwiseDealerLast;
    private final Community community;


    public ChanceSequence(
            List<Hole> clockwiseDealerLast,
            Community community)
    {
        this.clockwiseDealerLast = ImmutableList.copyOf(clockwiseDealerLast);
        this.community = Preconditions.checkNotNull(community);
    }
}
