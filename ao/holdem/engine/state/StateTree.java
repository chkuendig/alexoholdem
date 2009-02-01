package ao.holdem.engine.state;

import ao.holdem.model.Avatar;
import ao.holdem.model.act.AbstractAction;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

/**
 * Date: Feb 1, 2009
 * Time: 3:28:32 PM
 */
public class StateTree
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(StateTree.class);

    private StateTree() {}


    //--------------------------------------------------------------------
    private static final Node ROOT;

    static
    {
        LOG.debug("computing heads-up");
        ROOT = new Node(new State(Arrays.asList(
            Avatar.local("dealee"), Avatar.local("dealer"))));
        LOG.debug("done");
    }

    //--------------------------------------------------------------------
    public static Node headsUpRoot()
    {
        return ROOT;
    }


    //--------------------------------------------------------------------
    public static class Node
    {
        //----------------------------------------------------------------
        private final State                         STATE;
        private final EnumMap<AbstractAction, Node> KIDS;


        private Node(State state)
        {
            STATE = state;
            KIDS  = new EnumMap<AbstractAction, Node>(
                          AbstractAction.class);
            for (Map.Entry<AbstractAction, State> act :
                    state.viableActions().entrySet())
            {
                KIDS.put(act.getKey(),
                         new Node(act.getValue()));
            }
        }


        //----------------------------------------------------------------
        public State state()
        {
            return STATE;
        }

        public Map<AbstractAction, Node> acts()
        {
            return KIDS;
        }
    }
}
