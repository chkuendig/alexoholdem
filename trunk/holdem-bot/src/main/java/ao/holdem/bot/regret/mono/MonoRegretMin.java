package ao.holdem.bot.regret.mono;

import ao.holdem.abs.bucket.abstraction.access.odds.IBucketOdds;
import ao.holdem.engine.state.HeadsUpStatus;
import ao.holdem.engine.state.tree.StateTree;
import ao.holdem.model.act.AbstractAction;
import ao.holdem.bot.regret.InfoMatrix;
import ao.holdem.bot.regret.InfoPart;
import ao.holdem.bot.regret.IterativeMinimizer;

/**
 * User: alex
 * Date: 20-Apr-2009
 * Time: 10:10:13 PM
 */
public class MonoRegretMin implements IterativeMinimizer
{
    //--------------------------------------------------------------------
    private final IBucketOdds ODDS;
    private final InfoPart    INFO;

    private       boolean     updateDealee;
    private       double      showdownStakesFactor;


    //--------------------------------------------------------------------
    public MonoRegretMin(
            InfoPart info, IBucketOdds odds)
    {
        INFO = info;
        ODDS = odds;
    }


    //--------------------------------------------------------------------
    public void exploit(char absDealerBuckets[],
                        char absDealeeBuckets[]) {
        iterate(absDealerBuckets, absDealeeBuckets, false);
    }

    public void iterate(char absDealerBuckets[],
                        char absDealeeBuckets[]) {
        iterate(absDealerBuckets, absDealeeBuckets, true);
    }

    private void iterate(char    absDealerBuckets[],
                         char    absDealeeBuckets[],
                         boolean dynamicDealee)
    {
        showdownStakesFactor =
                (ODDS.nonLossProb(
                        absDealerBuckets[3],
                        absDealeeBuckets[3]) - 0.5) * 2.0;

        updateDealee = dynamicDealee;
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
        boolean dealerProp  = node.dealerIsNext();
        char    roundBucket =
                    (dealerProp
                    ? absDealerBuckets
                    : absDealeeBuckets)[ node.round().ordinal() ];

        InfoMatrix.InfoSet infoSet =
                INFO.infoSet(node, roundBucket);

        double strategy[] = infoSet.strategy();

        if ((dealerProp && pDealee == 0) ||
                ((!dealerProp) && pDealer == 0)) {
            return approximate(
                    node, absDealerBuckets, absDealeeBuckets, strategy);
        } else {
            return approximateAndUpdateFully(
                    node, absDealerBuckets, absDealeeBuckets,
                    pDealer, pDealee, infoSet, strategy);
        }
    }


    //--------------------------------------------------------------------
    private double approximateAndUpdateFully(
            StateTree.Node     node,
            char               absDealerBuckets[],
            char               absDealeeBuckets[],
            double             pDealer,
            double             pDealee,
            InfoMatrix.InfoSet info,
            double             strategy[])
    {
        double counterfactualUtility = 0;
        double utilities[]           = {0, 0, 0};

        for (AbstractAction act : AbstractAction.VALUES) {
            StateTree.Node nextNode = node.kid(act);
            if (nextNode == null) continue;

            double val; // from POV of dealer
            double actProb = strategy[ act.ordinal() ];
            HeadsUpStatus  status   = nextNode.status();
            if (status != HeadsUpStatus.IN_PROGRESS) {
                val = evaluate(nextNode);
            } else {
                val = approximateAndUpdate(
                        nextNode,
                        absDealerBuckets,
                        absDealeeBuckets,
                        pDealer * (node.dealerIsNext() ? actProb : 1.0),
                        pDealee * (node.dealerIsNext() ? 1.0 : actProb));
            }

            utilities[ act.ordinal() ]  = val;
            counterfactualUtility      += val * actProb;
        }

        double oppReachingFactor =
                node.dealerIsNext() ? pDealee : -pDealer;
        if (oppReachingFactor != 0) {
            double immediateCounterfactualRegret[] =
                new double[ AbstractAction.VALUES.length ];

            for (AbstractAction act : AbstractAction.VALUES) {
                double cRegret =
                        (utilities[ act.ordinal() ]
                                - counterfactualUtility)
                            * oppReachingFactor;
                immediateCounterfactualRegret[ act.ordinal() ] = cRegret;
            }

            if (updateDealee || node.dealerIsNext()) {
                info.add(immediateCounterfactualRegret);
            }
        }
        if (updateDealee || node.dealerIsNext()) {
            info.add(strategy, node.dealerIsNext() ? pDealer : pDealee);
        }

        return counterfactualUtility;
    }


    //--------------------------------------------------------------------
    private double approximate(
            StateTree.Node node,
            char           absDealerBuckets[],
            char           absDealeeBuckets[],
            double         strategy[])
    {
        double expectedValue = 0;
        for (AbstractAction act : AbstractAction.VALUES) {
            double         actProb  = strategy[ act.ordinal() ];
            StateTree.Node nextNode = node.kid( act );
            if (actProb == 0 || nextNode == null) continue;

            expectedValue += actProb *
                   ((nextNode.status() != HeadsUpStatus.IN_PROGRESS)
                    ? evaluate(
                            nextNode)
                    : approximateAndUpdate(
                            nextNode,
                            absDealerBuckets,
                            absDealeeBuckets,
                            0, 0));
        }
        return expectedValue;
    }


    //--------------------------------------------------------------------
    // from POV of dealer
    private double evaluate(
            StateTree.Node node)
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
    public int count(
            StateTree.Node node,
            char           absDealerBuckets[],
            char           absDealeeBuckets[],
            boolean        includeZeroes)
    {
        if (node.status() != HeadsUpStatus.IN_PROGRESS) return 1;

        boolean dealerProp  = node.dealerIsNext();
        char    roundBucket =
                    (dealerProp
                    ? absDealerBuckets
                    : absDealeeBuckets)[ node.round().ordinal() ];

        InfoMatrix.InfoSet infoSet =
                INFO.infoSet(node, roundBucket);

        double strategy[] = infoSet.strategy();

        int count = 0;
        for (AbstractAction act : AbstractAction.VALUES) {
            StateTree.Node nextNode = node.kid( act );
            if (nextNode == null) continue;

            double actProb = strategy[ act.ordinal() ];
            if (includeZeroes || actProb != 0) {
                count += count(nextNode,
                               absDealerBuckets,
                               absDealeeBuckets,
                               includeZeroes);
            }
        }

        return count;
    }
    

    //--------------------------------------------------------------------
    public void flush() {}
}
