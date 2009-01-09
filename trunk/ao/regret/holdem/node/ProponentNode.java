package ao.regret.holdem.node;

import ao.holdem.engine.state.State;
import ao.holdem.model.act.AbstractAction;
import ao.regret.InfoNode;
import ao.regret.holdem.HoldemBucket;
import ao.simple.kuhn.KuhnAction;
import ao.util.rand.Rand;
import ao.util.text.Txt;

import java.util.EnumMap;
import java.util.Map;

/**
 * Date: Jan 8, 2009
 * Time: 3:27:58 PM
 */
public class ProponentNode implements PlayerNode
{
    //--------------------------------------------------------------------
    private static final int SPARSE_LIMIT = 5;


    //--------------------------------------------------------------------
    private PlayerKids kids;
    private Map<AbstractAction, double[]> regret;
    private Map<AbstractAction, double[]> prob;
    private int                        visits = 0;


    //--------------------------------------------------------------------
    public ProponentNode(
            State        state,
            HoldemBucket bucket,
            boolean      forFirstToAct)
    {
        prob   = new EnumMap<AbstractAction, double[]>(
                        AbstractAction.class);
        regret = new EnumMap<AbstractAction, double[]>(
                        AbstractAction.class);
        kids   = new PlayerKids(state, bucket, forFirstToAct, true);

        EnumMap<AbstractAction,State> actions = state.validActions();
        for (AbstractAction action : actions.keySet())
        {
            prob.put(action, new double[]{
                                1.0 / actions.size()});
            regret.put(action, new double[]{0});
        }
    }


    //--------------------------------------------------------------------
    public AbstractAction nextAction()
    {
        AbstractAction bestAction       = null;
        double         bestActionWeight = Long.MIN_VALUE;

        for (Map.Entry<AbstractAction, double[]> p : prob.entrySet())
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

    public boolean actionAvalable(AbstractAction act)
    {
        return kids.actionAvalable( act );
    }


    //--------------------------------------------------------------------
    public InfoNode child(AbstractAction forAction)
    {
        return kids.child( forAction );
    }

    public double probabilityOf(AbstractAction action)
    {
        return prob.get( action )[ 0 ];
    }

    public boolean isInformed()
    {
        return visits >= SPARSE_LIMIT;
    }


    //--------------------------------------------------------------------
    public void add(Map<AbstractAction, Double> counterfactualRegret)
    {
        for (Map.Entry<AbstractAction, Double> r :
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
                p[0] = 1.0 / KuhnAction.VALUES.length;
            }
        }
        else
        {
            for (Map.Entry<AbstractAction, double[]>
                    p : prob.entrySet())
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
        for (Map.Entry<AbstractAction, InfoNode>
                action : kids.entrySet())
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
