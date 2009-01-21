package ao.regret.holdem.node;

import ao.holdem.engine.state.State;
import ao.holdem.model.act.AbstractAction;
import ao.regret.InfoNode;
import ao.regret.holdem.HoldemBucket;
import ao.util.math.rand.Rand;
import ao.util.text.Txt;

import java.util.Arrays;
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
    private double[]   regret;
    private double[]   prob;
    private int        visits = 0;


    //--------------------------------------------------------------------
    public ProponentNode(
            State        state,
            HoldemBucket bucket,
            boolean      forFirstToAct)
    {
        prob   = newActionTracker();
        regret = newActionTracker();
        kids   = new PlayerKids(state, bucket, forFirstToAct, true);

        EnumMap<AbstractAction,State> actions = state.viableActions();
        for (AbstractAction action : actions.keySet())
        {
            prob[   action.ordinal() ] = 1.0 / actions.size();
//            regret[ action.ordinal() ] = 0;
        }
    }

    private double[] newActionTracker()
    {
        double[] tracker = new double[ AbstractAction.VALUES.length ];
        Arrays.fill(tracker, Double.NaN);
        return tracker;
    }


    //--------------------------------------------------------------------
    public AbstractAction nextAction()
    {
        AbstractAction bestAction       = null;
        double         bestActionWeight = Long.MIN_VALUE;

        for (AbstractAction act : AbstractAction.VALUES)
        {
            if (Double.isNaN( prob[act.ordinal()] )) continue;

            double weight = Rand.nextDouble( prob[act.ordinal()] );
            if (bestActionWeight < weight)
            {
                bestAction       = act;
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
        return prob[ action.ordinal() ];
    }

    public boolean isInformed()
    {
        return visits >= SPARSE_LIMIT;
    }


    //--------------------------------------------------------------------
    public void add(double[] counterfactualRegret)
    {
        for (AbstractAction act : AbstractAction.VALUES)
        {
            double cRegret = counterfactualRegret[ act.ordinal() ];
            if (! Double.isNaN(cRegret))
            {
                regret[ act.ordinal() ] += cRegret;
            }
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
            // todo: is this branch only ever invoked on the first udate?
            for (AbstractAction act : AbstractAction.VALUES)
            {
                if (Double.isNaN( prob[act.ordinal()] )) continue;

                prob[ act.ordinal() ] = 1.0 / kids.size();
            }
        }
        else
        {
//            for (Map.Entry<AbstractAction, double[]>
//                    p : prob.entrySet())
            for (AbstractAction act : AbstractAction.VALUES)
            {
                if (Double.isNaN( regret[act.ordinal()] )) continue;
                double cRegret = regret[ act.ordinal() ];

                prob[ act.ordinal() ] =
                        (cRegret < 0)
                        ? 0
                        : cRegret / cumRegret;
            }
        }
    }

    private double positiveCumulativeCounterfactualRegret()
    {
        double positiveCumulation = 0;
        for (double pointRegret : regret)
        {
            if (Double.isNaN(pointRegret)) continue;
            if (pointRegret > 0)
            {
                positiveCumulation += pointRegret;
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
               .append( prob  [ action.getKey().ordinal() ] )
               .append( " :: " )
               .append( regret[ action.getKey().ordinal() ] / visits )
               .append( " :: " )
               .append( visits )
               .append( "\n" )
               .append( action.getValue().toString(depth + 1) )
               .append( "\n" );
        }
        return str.substring(0, str.length() - 1);
    }
}
