package ao.regret.holdem.node;

import ao.holdem.engine.state.State;
import ao.holdem.model.act.AbstractAction;
import ao.regret.InfoNode;
import ao.regret.holdem.HoldemBucket;

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
    private Map<AbstractAction, InfoNode> kids;


    //--------------------------------------------------------------------
    public PlayerKids(
            State        state,
            HoldemBucket bucket,
            boolean      forFirstToAct,
            boolean      asProponent)
    {
        kids = new EnumMap<AbstractAction, InfoNode>(
                    AbstractAction.class);

        EnumMap<AbstractAction, State> actions = state.viableActions();
        for (Map.Entry<AbstractAction, State>
                action : actions.entrySet())
        {
            State nextState = action.getValue();

            if (nextState.atEndOfHand())
            {
                kids.put(action.getKey(),
                         new TerminalNode(
                                 bucket, nextState));
            }
            else if (nextState.isStartOfRound())
            {
                kids.put(action.getKey(),
                         new BucketNode(bucket.nextBuckets(),
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


    //--------------------------------------------------------------------
    public Set<Entry<AbstractAction, InfoNode>> entrySet()
    {
        return kids.entrySet();
    }
}
