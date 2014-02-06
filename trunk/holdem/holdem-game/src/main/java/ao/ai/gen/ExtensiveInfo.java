package ao.ai.gen;

import ao.holdem.engine.state.tree.StateTree;
import ao.holdem.model.card.sequence.CardSequence;
import com.google.common.base.Preconditions;

/**
 *
 */
public class ExtensiveInfo
{
    private final StateTree.Node state;
    private final CardSequence chance;


    public ExtensiveInfo(
            StateTree.Node state,
            CardSequence chance)
    {
        this.state = Preconditions.checkNotNull(state);
        this.chance = Preconditions.checkNotNull(chance);
    }
}
