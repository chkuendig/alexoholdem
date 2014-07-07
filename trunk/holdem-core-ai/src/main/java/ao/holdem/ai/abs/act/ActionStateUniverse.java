package ao.holdem.ai.abs.act;

import ao.holdem.engine.state.ActionState;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public enum ActionStateUniverse
{;
    private static Iterable<ActionState> headsUpActionDecisionStates;

    public static Iterable<ActionState> headsUpActionDecisionStates() {
        if (headsUpActionDecisionStates != null) {
            return headsUpActionDecisionStates;
        }


        Set<ActionState> states = new LinkedHashSet<>();

        ActionState root = ActionState.autoBlindInstance(2);
        traverse(root, states);

        headsUpActionDecisionStates = states;
        return states;
    }


    private static void traverse(ActionState node, Collection<ActionState> buffer) {
        if (! node.atEndOfHand()) {
            buffer.add(node);
        }

        for (ActionState child : node.actions(false).values()) {
            traverse(child, buffer);
        }
    }
}
