package ao.holdem.gen;

import ao.holdem.engine.state.tree.StateTree;
import com.google.common.base.Preconditions;

/**
 *
 */
public class ExtensiveHoldemState
{
    private final StateTree.Node actionSequence;
    private final ChanceSequence chanceSequence;


    public ExtensiveHoldemState(
            StateTree.Node actionSequence,
            ChanceSequence chanceSequence)
    {
        this.actionSequence = Preconditions.checkNotNull(actionSequence);
        this.chanceSequence = Preconditions.checkNotNull(chanceSequence);
    }
}
