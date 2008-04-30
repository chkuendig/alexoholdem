package ao.regret.node;

import ao.regret.Regret;
import ao.simple.KuhnAction;
import ao.simple.rules.KuhnRules;

import java.util.Map;

/**
 *
 */
public class ProponentNode extends PlayerNode
{
    //--------------------------------------------------------------------
    private Map<KuhnAction, Regret>   averageRegrets;

    private Map<KuhnAction, InfoNode> actions;

    private double probabilityUpToThisPoint;


    //--------------------------------------------------------------------
    public ProponentNode(
            KuhnRules rules)
    {
        for (Map.Entry<KuhnAction, KuhnRules> transition :
                rules.transitions().entrySet())
        {
            KuhnRules state = transition.getValue();
            state.
        }
    }
}
