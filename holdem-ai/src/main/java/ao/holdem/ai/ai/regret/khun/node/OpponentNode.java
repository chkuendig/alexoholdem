package ao.holdem.ai.ai.regret.khun.node;

import ao.holdem.ai.ai.regret.InfoNode;
import ao.holdem.ai.ai.simple.kuhn.KuhnAction;
import ao.holdem.ai.ai.simple.kuhn.rules.KuhnBucket;
import ao.holdem.ai.ai.simple.kuhn.rules.KuhnRules;
import ao.holdem.ai.ai.simple.kuhn.state.StateFlow;
import ao.util.text.Txt;

import java.util.EnumMap;
import java.util.Map;

/**
 * 
 */
public class OpponentNode implements PlayerNode
{
    //--------------------------------------------------------------------
    private Map<KuhnAction, InfoNode> kids;


    //--------------------------------------------------------------------
    public OpponentNode(KuhnRules  rules,
                        KuhnBucket bucket)
    {
        kids = new EnumMap<KuhnAction, InfoNode>(KuhnAction.class);

        for (Map.Entry<KuhnAction, KuhnRules> transition :
                rules.transitions().entrySet())
        {
            KuhnRules nextRules = transition.getValue();
            StateFlow nextState = nextRules.state();

            if (nextState.endOfHand())
            {
                kids.put(transition.getKey(),
                         new TerminalNode(
                                 bucket,
                                 nextState.outcome()));
            }
            else
            {
                kids.put(transition.getKey(),
                         new ProponentNode(
                                 nextRules, bucket));
            }
        }
    }


    //--------------------------------------------------------------------
    public InfoNode child(KuhnAction forAction)
    {
        return kids.get( forAction );
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return toString(0);
    }

    public String toString(int depth)
    {
        StringBuilder str = new StringBuilder();
        for (Map.Entry<KuhnAction, InfoNode> kid : kids.entrySet())
        {
            str.append( Txt.nTimes("\t", depth) )
               .append( kid.getKey() )
               .append( "\n" )
               .append( kid.getValue().toString(depth + 1) )
               .append( "\n" );
        }
        return str.substring(0, str.length()-1);
    }
}
