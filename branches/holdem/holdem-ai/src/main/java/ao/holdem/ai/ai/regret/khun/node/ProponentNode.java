package ao.holdem.ai.ai.regret.khun.node;

import ao.holdem.ai.ai.regret.InfoNode;
import ao.holdem.ai.ai.simple.kuhn.KuhnAction;
import ao.holdem.ai.ai.simple.kuhn.rules.KuhnBucket;
import ao.holdem.ai.ai.simple.kuhn.rules.KuhnRules;
import ao.holdem.ai.ai.simple.kuhn.state.StateFlow;
import ao.util.math.rand.Rand;
import ao.util.text.Txt;

import java.util.EnumMap;
import java.util.Map;

/**
 *
 */
public class ProponentNode implements PlayerNode
{
    //--------------------------------------------------------------------
    private Map<KuhnAction, double[]> regret; // mutable doubles
    private Map<KuhnAction, InfoNode> actions;
//    private Map<KuhnAction, double[]> prob;  // mutable doubles
    private int                       visits = 0;

    private Map<KuhnAction, double[]> probSum; // mutable doubles
    private double                    reachSum;


    //--------------------------------------------------------------------
    public ProponentNode(KuhnRules rules, KuhnBucket bucket)
    {
//        prob    = new EnumMap<KuhnAction, double[]>(KuhnAction.class);
        regret  = new EnumMap<KuhnAction, double[]>(KuhnAction.class);
        probSum = new EnumMap<KuhnAction, double[]>(KuhnAction.class);
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

        for (KuhnAction act : KuhnAction.VALUES)
        {
//            prob.put(act, new double[]{
//                             1.0 / KuhnAction.VALUES.length});
            regret .put(act, new double[]{0});
            probSum.put(act, new double[]{0});
        }
    }


    //--------------------------------------------------------------------
    public KuhnAction nextAction()
    {
        double passProb = averageStrategy()[ 0 ];

        return Rand.nextBoolean( passProb )
                ? KuhnAction.PASS
                : KuhnAction.BET;
    }

    public double[] averageStrategy()
    {
        return new double[]{
                probSum.get(KuhnAction.PASS)[0] / reachSum,
                probSum.get(KuhnAction.BET )[0] / reachSum};
    }



    //--------------------------------------------------------------------
    public double probabilityOf(KuhnAction action)
    {
        return probabilities()[ action.ordinal() ];
    }

    public InfoNode child(KuhnAction forAction)
    {
        return actions.get( forAction );
    }


    //--------------------------------------------------------------------
    public void add(
            Map<KuhnAction, Double> counterfactualRegret,
            double                  proponentReachProb)
    {
        double prob[] = probabilities();
        for (KuhnAction act : KuhnAction.VALUES) {
            probSum.get(act)[0] +=
                    proponentReachProb * prob[ act.ordinal() ];
        }
        reachSum += proponentReachProb;

        for (Map.Entry<KuhnAction, Double> r :
                counterfactualRegret.entrySet())
        {
            regret.get( r.getKey() )[0] += r.getValue();
        }

        visits++;
    }


    //--------------------------------------------------------------------
    private double[] probabilities()
    {
        double prob[]    = new double[2];
        double cumRegret = positiveCumulativeCounterfactualRegret();

        if (cumRegret <= 0)
        {
            prob[0] = prob[1] = 1.0 / 2;
        }
        else
        {
            prob[0] = Math.max(0,
                        regret.get( KuhnAction.PASS )[0] / cumRegret);

            prob[1] = Math.max(0,
                        regret.get( KuhnAction.BET  )[0] / cumRegret);
        }

        return prob;
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
    public String toString(KuhnAction action)
    {
        return new StringBuilder()
                .append( action )
                .append( " :: " )
                .append( averageStrategy()[ action.ordinal() ] )
                .append( " :: " )
                .append( probabilities()  [ action.ordinal() ] )
                .append( " :: " )
                .append( regret.get(action )[0] / visits)
                .append( " :: " )
                .append( visits )
                .toString();

    }

    public String toString(int depth)
    {
        StringBuilder str = new StringBuilder();
        for (Map.Entry<KuhnAction, InfoNode> action : actions.entrySet())
        {
            str.append( Txt.nTimes("\t", depth) )
               .append( action.getKey() )
               .append( " :: " )
               .append( averageStrategy()[ action.getKey().ordinal() ] )
               .append( " :: " )
               .append( probabilities()[ action.getKey().ordinal() ] )
               .append( " :: " )
               .append( regret.get(action.getKey())[0] / visits)
               .append( " :: " )
               .append( visits )
               .append( "\n" )
               .append( action.getValue().toString(depth + 1) )
               .append( "\n" );
        }
        return str.substring(0, str.length()-1);
    }
}
