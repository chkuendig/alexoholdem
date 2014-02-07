package ao.ai.gen;

import ao.holdem.engine.state.tree.StateTree;
import ao.holdem.model.act.AbstractAction;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.learn.mst.gen5.ExtensiveGame;
import ao.learn.mst.gen5.ExtensiveGame$class;
import ao.learn.mst.gen5.node.ExtensiveNode;
import ao.learn.mst.gen5.node.ExtensiveStateNode;
import com.google.common.collect.ImmutableList;

/**
 * 03/02/14 10:22 PM
 */
public class ExtensiveHoldemGame
    implements ExtensiveGame<ExtensiveHoldemState, ExtensiveInfo, AbstractAction>
{
    @Override
    public int playerCount() {
        return 2;
    }

    @Override
    public ExtensiveHoldemState initialState() {
        return new ExtensiveHoldemState(
                StateTree.headsUpRoot(),
                new ChanceSequence(
                        ImmutableList.<Hole>of(),
                        Community.PREFLOP
                ));
    }

    @Override @SuppressWarnings("unchecked")
    public ExtensiveStateNode<ExtensiveHoldemState, ExtensiveInfo, AbstractAction> initialStateNode() {
        return (ExtensiveStateNode) ExtensiveGame$class.initialStateNode(this);
    }

    @Override
    public ExtensiveNode<ExtensiveInfo, AbstractAction> node(ExtensiveHoldemState state) {
        return null;
    }

    @Override @SuppressWarnings("unchecked")
    public ExtensiveStateNode<ExtensiveHoldemState, ExtensiveInfo, AbstractAction> stateNode(ExtensiveHoldemState state) {
//        return (ExtensiveStateNode) ExtensiveGame$class.stateNode(this, state);
        return null;
    }

    @Override
    public ExtensiveHoldemState transition(ExtensiveHoldemState nonTerminal, AbstractAction action) {
        return null;
    }

    @Override
    public ExtensiveStateNode<ExtensiveHoldemState, ExtensiveInfo, AbstractAction> transitionStateNode(
            ExtensiveStateNode<ExtensiveHoldemState, ExtensiveInfo, AbstractAction> nonTerminal, AbstractAction action) {
        return null;
    }
}
