package ao.regret.holdem.node;

import ao.holdem.engine.state.StateTree;
import ao.holdem.model.act.AbstractAction;
import ao.regret.InfoNode;
import ao.regret.holdem.HoldemBucket;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Date: Jan 8, 2009
 * Time: 4:59:53 PM
 */
public class PlayerKids
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(PlayerKids.class);


    //--------------------------------------------------------------------
    private EnumMap<AbstractAction, InfoNode> kids;


    //--------------------------------------------------------------------
    public PlayerKids(
            StateTree.Node state,
            HoldemBucket   bucket,
            boolean        forFirstToAct,
            boolean        asProponent)
    {
        kids = new EnumMap<AbstractAction, InfoNode>(
                    AbstractAction.class);

        Map<AbstractAction, StateTree.Node> actions = state.acts();
        for (Map.Entry<AbstractAction, StateTree.Node>
                action : actions.entrySet())
        {
//            LOG.debug("exploring " + action.getKey());
            StateTree.Node nextState = action.getValue();

            if (nextState.state().atEndOfHand())
            {
                kids.put(action.getKey(),
                         new TerminalNode(
                                 bucket,
                                 state.state().dealerIsNext(),
                                 nextState));
            }
            else if (nextState.state().isStartOfRound())
            {
                kids.put(action.getKey(),
                         new BucketNode(bucket,
                                        nextState,
                                        forFirstToAct));
            }
            else
            {
                kids.put(action.getKey(),
                         asProponent
                         ? new OpponentNode(
                                 nextState, bucket, forFirstToAct)
                         : new ProponentNode(
                                 nextState, bucket, forFirstToAct));
            }
        }
    }


    //--------------------------------------------------------------------
    public boolean actionAvalable(AbstractAction act)
    {
        return kids.containsKey( act );
    }

    public InfoNode child(AbstractAction forAction)
    {
        return kids.get( forAction );
    }

    public int size()
    {
        return kids.size();
    }


    //--------------------------------------------------------------------
    public Set<Entry<AbstractAction, InfoNode>> entrySet()
    {
        return kids.entrySet();
    }
    public Collection<AbstractAction> acts()
    {
        return kids.keySet();
    }
}
