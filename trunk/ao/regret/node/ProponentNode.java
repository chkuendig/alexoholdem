package ao.regret.node;

import ao.bucket.Bucket;
import ao.simple.KuhnAction;
import ao.simple.rules.KuhnRules;
import ao.simple.state.StateFlow;
import ao.util.text.Txt;

import java.util.EnumMap;
import java.util.Map;

/**
 *
 */
public class ProponentNode implements PlayerNode
{
    //--------------------------------------------------------------------
//    private Map<KuhnAction, Regret>   averageRegrets;
    private Map<KuhnAction, InfoNode> actions;

//    private double probabilityUpToThisPoint;


    //--------------------------------------------------------------------
    public ProponentNode(KuhnRules rules, Bucket bucket)
    {
        actions = new EnumMap<KuhnAction, InfoNode>(KuhnAction.class);

        for (Map.Entry<KuhnAction, KuhnRules> transition :
                rules.transitions().entrySet())
        {
            KuhnRules nextRules = transition.getValue();
            StateFlow nextState = nextRules.state();

            if (nextState.endOfHand())
            {
                actions.put(transition.getKey(),
                            new TerminalNode(
                                    bucket, nextState.outcome()));
            }
            else
            {
                actions.put(transition.getKey(),
                            new OpponentNode(nextRules, bucket));
            }
        }
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return toString(0);
    }

    public String toString(int depth)
    {
        StringBuilder str = new StringBuilder();
        for (Map.Entry<KuhnAction, InfoNode> action : actions.entrySet())
        {
            str.append( Txt.nTimes("\t", depth) )
               .append( action.getKey() )
               .append( "\n" )
               .append( action.getValue().toString(depth + 1) )
               .append( "\n" );
        }
        return str.toString();
    }
}
