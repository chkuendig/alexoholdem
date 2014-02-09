package ao.holdem.bot.regret.serial;

import ao.holdem.abs.bucket.abstraction.access.odds.IBucketOdds;
import ao.holdem.engine.state.HeadsUpStatus;
import ao.holdem.engine.state.tree.StateTree;
import ao.holdem.model.act.AbstractAction;
import ao.holdem.bot.regret.InfoMatrix;
import ao.holdem.bot.regret.InfoPart;
import ao.holdem.bot.regret.IterativeMinimizer;

/**
 * User: alex
 * Date: 26-Apr-2009
 * Time: 11:03:24 PM
 */
public class RegMin implements IterativeMinimizer
{
    //--------------------------------------------------------------------
//    private final ExecutorService EXEC;

    private final InfoPart    INFO;
    private final IBucketOdds ODDS;
    private final double      AGGRESSION; // UofA bot uses 7%

    private       char        DEALER_BUCKETS[];
    private       char        DEALEE_BUCKETS[];

    private       boolean     forDealer;
    private       double      showdownStakesFactor;


    //--------------------------------------------------------------------
    public RegMin(InfoPart        info
            ,     IBucketOdds     odds
            ,     double          aggression
//            ,     ExecutorService exec
            )
                    // 1.0 means neutral, >1 means aggresive
    {
//        EXEC       = exec;
        INFO       = info;
        ODDS       = odds;
        AGGRESSION = aggression;
    }


    //--------------------------------------------------------------------
    public void iterate(
            char absDealerBuckets[],
            char absDealeeBuckets[])
    {
        DEALER_BUCKETS = absDealerBuckets;
        DEALEE_BUCKETS = absDealeeBuckets;

        showdownStakesFactor =
                (ODDS.nonLossProb(
                        DEALER_BUCKETS[3],
                        DEALEE_BUCKETS[3]) - 0.5) * 2.0;

        forDealer = true;
        advanceOrPassRegret(StateTree.headsUpRoot(),  1.0);

        forDealer = false;
        advanceOrPassRegret(StateTree.headsUpRoot(), -1.0);
    }


    //--------------------------------------------------------------------
    private double advanceOrPassRegret(
            StateTree.Node node,
            double         oppReach)
    {
        if (node.status() != HeadsUpStatus.IN_PROGRESS) {
            return evaluate(node);
        }

        InfoMatrix.InfoSet info       = infoSet(node);
        double             strategy[] = info.strategy();

        if (oppReach == 0) {
            return approximate  (node, strategy);
        } else if (node.dealerIsNext() == forDealer) {
            return advanceRegret(node, oppReach, info, strategy);
        } else {
            return passRegret   (node, oppReach, strategy);
        }
    }


    //--------------------------------------------------------------------
    private double advanceRegret(
            StateTree.Node     node,
            double             oppReach,
            InfoMatrix.InfoSet info,
            double             strategy[])
    {
        double counterfactualUtility = 0;
        double utilities[]           = {0, 0, 0};

        for (AbstractAction act : AbstractAction.VALUES) {
            StateTree.Node nextNode = node.kid(act);
            if (nextNode == null) continue;

            double actProb = strategy[ act.ordinal() ];
            double val     = advanceOrPassRegret(nextNode, oppReach);

            utilities[ act.ordinal() ]  = val;
            counterfactualUtility      += val * actProb;
        }

        double immediateCounterfactualRegret[] =
                new double[ AbstractAction.VALUES.length ];
        for (AbstractAction act : AbstractAction.VALUES) {
            if (node.kid(act) == null) continue;
            double cRegret = oppReach *
                    (utilities[ act.ordinal() ] - counterfactualUtility);

            immediateCounterfactualRegret[ act.ordinal() ] = cRegret;
        }

        info.add( immediateCounterfactualRegret );
        return counterfactualUtility;
    }


    //--------------------------------------------------------------------
    private double passRegret(
            StateTree.Node node,
            double         oppReach,
            double         strategy[])
    {
        double counterfactualUtility = 0;
        for (AbstractAction act : AbstractAction.VALUES) {
            StateTree.Node nextNode = node.kid(act);
            if (nextNode == null) continue;

            double actProb = strategy[ act.ordinal() ];
            if (actProb == 0) continue;

            double val     = advanceOrPassRegret(
                    nextNode, oppReach * actProb);

            // todo gotta test this replacement for if (val > 0)
            if (forDealer && val > 0 ||
                    !forDealer && val < 0) {
                val *= AGGRESSION;
            }

            counterfactualUtility += val * actProb;
        }

        return counterfactualUtility;
    }


    //--------------------------------------------------------------------
    private double approximate(
            StateTree.Node node,
            double         strategy[])
    {
        double expectedValue = 0;
        for (AbstractAction act : AbstractAction.VALUES) {
            double         actProb  = strategy[ act.ordinal() ];
            StateTree.Node nextNode = node.kid( act );
            if (actProb == 0 || nextNode == null) continue;

            expectedValue += actProb *
                   advanceOrPassRegret(nextNode, 0);
        }
        return expectedValue;
    }


    //--------------------------------------------------------------------
    // from POV of dealer
    private double evaluate(StateTree.Node node)
    {
        switch (node.status()) {
            case SHOWDOWN:
                return node.stakes() * showdownStakesFactor;

            case DEALER_WINS: return  node.dealeeCommit();
            case DEALEE_WINS: return -node.dealerCommit();
        }
        return Double.NaN;
    }


    //--------------------------------------------------------------------
    private InfoMatrix.InfoSet infoSet(StateTree.Node node)
    {
        char roundBucket =
                (node.dealerIsNext()
                 ? DEALER_BUCKETS
                 : DEALEE_BUCKETS)[ node.round().ordinal() ];

        return INFO.infoSet(node, roundBucket);
    }


    //--------------------------------------------------------------------
    public void flush() {}
}
