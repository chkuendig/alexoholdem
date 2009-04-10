package ao.regret.holdem;

import ao.bucket.abstraction.access.odds.IBucketOdds;
import ao.holdem.engine.state.HeadsUpStatus;
import ao.holdem.engine.state.tree.StateTree;
import ao.holdem.engine.state.tree.StateTree.Node;
import ao.holdem.model.act.AbstractAction;
import ao.regret.holdem.InfoBranch.InfoSet;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * User: shalom
 * Date: Feb 26, 2009
 * Time: 1:16:44 PM
 *
 * Note: NOT threadsafe!
 */
public class RegretMinimizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(RegretMinimizer.class);

    private static final double APPROX_THRESHOLD = 1.0 / (1000 * 1000);


    //--------------------------------------------------------------------
    private final IBucketOdds ODDS;
    private final InfoTree    INFO;


    //--------------------------------------------------------------------
    public RegretMinimizer(
            InfoTree info, IBucketOdds odds)
    {
        INFO = info;
        ODDS = odds;
    }


    //--------------------------------------------------------------------
    public void minimize(char    absDealerBuckets[],
                         char    absDealeeBuckets[])
    {
        approximateAndUpdate(
                StateTree.headsUpRoot(),
                absDealerBuckets,
                absDealeeBuckets,
                1.0, 1.0);
    }


    //--------------------------------------------------------------------
    private double approximateAndUpdate(
            StateTree.Node node,
            char           absDealerBuckets[],
            char           absDealeeBuckets[],
            double         pDealer,
            double         pDealee)
    {
        boolean   dealerProp = node.dealerIsNext();
        if (      dealerProp  && pDealee == 0 ||
                (!dealerProp) && pDealer == 0) {

//            int    withZeroes = count(
//                    node, absDealerBuckets, absDealeeBuckets, true);
//            int withoutZeroes = count(
//                    node, absDealerBuckets, absDealeeBuckets, false);
//            LOG.debug("saving " + (withZeroes - withoutZeroes) +
//                       " over " +  withZeroes);
            return approximate(node, absDealerBuckets, absDealeeBuckets);
        }

        boolean canRaise      = node.canRaise();
        double  expectedValue = 0;
        double  expectation[] = {0, 0, 0};

        InfoBranch infoBranch = INFO.info(
                node.pathToFlop(), node.round());

        char roundBucket =
                (dealerProp
                ? absDealerBuckets
                : absDealeeBuckets)[ node.round().ordinal() ];
        InfoSet info = infoBranch.get(roundBucket, node.roundPathId());

        double probabilities[] =
                info.probabilities(canRaise, node.canCheck());

        Map<AbstractAction, Node> acts = node.acts();
        for (Map.Entry<AbstractAction, Node> next : acts.entrySet())
        {
            double actProb = probabilities[ next.getKey().ordinal() ];

            double         val; // from POV of dealer
            StateTree.Node nextNode = next.getValue();
            HeadsUpStatus  status   = nextNode.status();
            if (status != HeadsUpStatus.IN_PROGRESS) {
                val = evaluate(
                        nextNode, absDealerBuckets, absDealeeBuckets);
            } else {
                val = approximateAndUpdate(
                            nextNode,
                            absDealerBuckets,
                            absDealeeBuckets,
                            pDealer * (dealerProp ? actProb : 1.0),
                            pDealee * (dealerProp ? 1.0 : actProb));
            }

            expectation[ next.getKey().ordinal() ] = val;
            expectedValue += val * actProb;
        }

        double[] counterfactualRegret =
                new double[ AbstractAction.VALUES.length ];
        for (AbstractAction act : acts.keySet())
        {
            double cRegret = (expectation[ act.ordinal() ] - expectedValue);

            if (dealerProp) {
                cRegret *=  pDealee;
            } else {
                cRegret *= -pDealer;
            }
            counterfactualRegret[ act.ordinal() ] = cRegret;
        }
        info.add(counterfactualRegret, canRaise);

        return expectedValue;
    }


    //--------------------------------------------------------------------
    private double approximate(
            StateTree.Node node,
            char           absDealerBuckets[],
            char           absDealeeBuckets[])
    {
        if (node.status() != HeadsUpStatus.IN_PROGRESS) {
            return evaluate(node, absDealerBuckets, absDealeeBuckets);
        }

        boolean dealerProp    = node.dealerIsNext();
        double  expectedValue = 0;

        InfoBranch infoBranch = INFO.info(
                node.pathToFlop(), node.round());

        char roundBucket =
                (dealerProp
                ? absDealerBuckets
                : absDealeeBuckets)[ node.round().ordinal() ];
        InfoSet info = infoBranch.get(roundBucket, node.roundPathId());

        double probabilities[] =
                info.probabilities(node.canRaise(), node.canCheck());

        Map<AbstractAction, Node> acts = node.acts();
        for (Map.Entry<AbstractAction, Node> next : acts.entrySet())
        {
            double actProb = probabilities[ next.getKey().ordinal() ];
            if (actProb != 0) {
                double val = approximate(
                                next.getValue(),
                                absDealerBuckets,
                                absDealeeBuckets);
                expectedValue += val * actProb;
            }
        }

        return expectedValue;
    }


    //--------------------------------------------------------------------
    private int count(
            StateTree.Node node,
            char           absDealerBuckets[],
            char           absDealeeBuckets[],
            boolean        includeZeroes)
    {
        if (node.status() != HeadsUpStatus.IN_PROGRESS) return 1;

        InfoBranch infoBranch = INFO.info(
                node.pathToFlop(), node.round());

        char roundBucket =
                (node.dealerIsNext()
                ? absDealerBuckets
                : absDealeeBuckets)[ node.round().ordinal() ];
        InfoSet info = infoBranch.get(roundBucket, node.roundPathId());

        double probabilities[] =
                info.probabilities(node.canRaise(), node.canCheck());

        int                       count = 0;
        Map<AbstractAction, Node> acts  = node.acts();
        for (Map.Entry<AbstractAction, Node> next : acts.entrySet())
        {
            double actProb = probabilities[ next.getKey().ordinal() ];
            if (includeZeroes || actProb != 0) {
                count += count(next.getValue(),
                               absDealerBuckets,
                               absDealeeBuckets,
                               includeZeroes);
            }
        }

        return count;
    }


    //--------------------------------------------------------------------
    // from POV of dealer
    private double evaluate(
            StateTree.Node node,
            char           absDealerBuckets[],
            char           absDealeeBuckets[])
    {
        switch (node.status()) {
            case SHOWDOWN:
                return node.stakes() * (
                        ODDS.nonLossProb(
                                absDealerBuckets[3],
                                absDealeeBuckets[3]) - 0.5) * 2.0;

            case DEALER_WINS: return  node.dealeeCommit();
            case DEALEE_WINS: return -node.dealerCommit();
        }

        return Double.NaN;
    }
}