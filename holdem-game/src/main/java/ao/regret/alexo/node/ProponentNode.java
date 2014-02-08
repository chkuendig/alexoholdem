package ao.regret.alexo.node;

import ao.regret.InfoNode;
import ao.regret.alexo.AlexoBucket;
import ao.simple.alexo.AlexoAction;
import ao.simple.alexo.state.AlexoState;
import ao.util.math.rand.Rand;
import ao.util.text.Txt;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class ProponentNode implements PlayerNode
{
    //--------------------------------------------------------------------
    private static final int SPARSE_LIMIT = 5;


    //--------------------------------------------------------------------
    private PlayerKids                 kids;
    private Map<AlexoAction, double[]> regret;
    private Map<AlexoAction, double[]> prob;
    private int                        visits = 0;


    //--------------------------------------------------------------------
    public ProponentNode(
            AlexoState  state,
            AlexoBucket bucket,
            boolean     forFirstToAct)
    {
        prob   = new EnumMap<AlexoAction, double[]>(AlexoAction.class);
        regret = new EnumMap<AlexoAction, double[]>(AlexoAction.class);
        kids   = new PlayerKids(state, bucket, forFirstToAct, true);

        List<AlexoAction> actions = state.validActions();
        for (AlexoAction action : actions)
        {
            prob.put(action, new double[]{
                                1.0 / actions.size()});
            regret.put(action, new double[]{0});
        }
    }


    //--------------------------------------------------------------------
    public AlexoAction nextAction()
    {
        AlexoAction bestAction       = null;
        double      bestActionWeight = Long.MIN_VALUE;

        for (Map.Entry<AlexoAction, double[]> p : prob.entrySet())
        {
            double weight = Rand.nextDouble(p.getValue()[0]);
            if (bestActionWeight < weight)
            {
                bestAction       = p.getKey();
                bestActionWeight = weight;
            }
        }

        return bestAction;
    }

    public boolean actionAvalable(AlexoAction act)
    {
        return kids.actionAvalable( act );
    }


    //--------------------------------------------------------------------
    public InfoNode child(AlexoAction forAction)
    {
        return kids.child( forAction );
    }

    public double probabilityOf(AlexoAction action)
    {
        return prob.get( action )[ 0 ];
    }

    public boolean isInformed()
    {
        return visits >= SPARSE_LIMIT;
    }


    //--------------------------------------------------------------------
    public void add(Map<AlexoAction, Double> counterfactualRegret)
    {
        for (Map.Entry<AlexoAction, Double> r :
                counterfactualRegret.entrySet())
        {
            regret.get( r.getKey() )[0] += r.getValue();
        }

        visits++;
        updateActionPabilities();
    }


    //--------------------------------------------------------------------
    private void updateActionPabilities()
    {
        double cumRegret = positiveCumulativeCounterfactualRegret();

        if (cumRegret <= 0)
        {
            for (double[] p : prob.values())
            {
                p[0] = 1.0 / AlexoAction.VALUES.length;
            }
        }
        else
        {
            for (Map.Entry<AlexoAction, double[]> p : prob.entrySet())
            {
                double cRegret = regret.get( p.getKey() )[0];

                p.getValue()[0] =
                        (cRegret < 0)
                        ? 0
                        : cRegret / cumRegret;
            }
        }
    }

    private double positiveCumulativeCounterfactualRegret()
    {
        double positiveCumulation = 0;
        for (double[] pointRegret : regret.values())
        {
            if (pointRegret[0] > 0)
            {
                positiveCumulation += pointRegret[0];
            }
        }
        return positiveCumulation;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return toString(0);
    }

    public String toString(int depth)
    {
        StringBuilder str = new StringBuilder();
        for (Map.Entry<AlexoAction, InfoNode> action : kids.entrySet())
        {
            str.append( Txt.nTimes("\t", depth) )
               .append( action.getKey() )
               .append( " :: " )
               .append( prob.get(action.getKey())[0] )
               .append( " :: " )
               .append( regret.get(action.getKey())[0] / visits )
               .append( " :: " )
               .append( visits )
               .append( "\n" )
               .append( action.getValue().toString(depth + 1) )
               .append( "\n" );
        }
        return str.substring(0, str.length()-1);
    }
}
