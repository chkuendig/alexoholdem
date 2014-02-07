package ao.holdem.ai.ai.regret.alexo.node;

import ao.holdem.ai.ai.regret.InfoNode;
import ao.holdem.ai.ai.regret.alexo.AlexoBucket;
import ao.holdem.ai.ai.simple.alexo.AlexoAction;
import ao.holdem.ai.ai.simple.alexo.state.AlexoState;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class PlayerKids
{
    //--------------------------------------------------------------------
    private Map<AlexoAction, InfoNode> kids;


    //--------------------------------------------------------------------
    public PlayerKids(
            AlexoState  state,
            AlexoBucket bucket,
            boolean     forFirstToAct,
            boolean     asProponent)
    {
        kids = new EnumMap<AlexoAction, InfoNode>(AlexoAction.class);

        List<AlexoAction> actions = state.validActions();
        for (AlexoAction action : actions)
        {
            AlexoState nextState = state.advance( action );

            if (nextState.endOfHand())
            {
                kids.put(action,
                         new TerminalNode(
                                 bucket, nextState));
            }
            else if (nextState.atStartOfRound())
            {
                kids.put(action,
                         new BucketNode(bucket.nextBuckets(),
                                        nextState,
                                        forFirstToAct));
            }
            else
            {
                kids.put(action,
                         asProponent
                         ? new OpponentNode(
                                 nextState, bucket, forFirstToAct)
                         : new ProponentNode(
                                 nextState, bucket, forFirstToAct));
            }
        }
    }


    //--------------------------------------------------------------------
    public boolean actionAvalable(AlexoAction act)
    {
        return kids.containsKey( act );
    }

    public InfoNode child(AlexoAction forAction)
    {
        return kids.get( forAction );
    }


    //--------------------------------------------------------------------
    public Set<Map.Entry<AlexoAction, InfoNode>> entrySet()
    {
        return kids.entrySet();
    }
}
