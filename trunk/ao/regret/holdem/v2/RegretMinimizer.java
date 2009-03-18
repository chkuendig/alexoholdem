package ao.regret.holdem.v2;

import ao.bucket.abstraction.access.odds.BucketOdds;
import ao.holdem.engine.state.HeadsUpStatus;
import ao.holdem.engine.state.State;
import ao.holdem.engine.state.tree.StateTree;
import ao.holdem.engine.state.tree.StateTree.Node;
import ao.holdem.model.act.AbstractAction;
import ao.regret.holdem.v2.InfoBranch.InfoSet;

import java.util.Arrays;
import java.util.Map;

/**
 * User: shalom
 * Date: Feb 26, 2009
 * Time: 1:16:44 PM
 */
public class RegretMinimizer
{
    //--------------------------------------------------------------------
    private final BucketOdds ODDS;
    private final InfoTree   INFO;


    //--------------------------------------------------------------------
    public RegretMinimizer(
            InfoTree info, BucketOdds odds)
    {
        INFO = info;
        ODDS = odds;
    }


    //--------------------------------------------------------------------
    public void minimize(char absDealerBuckets[],
                         char absDealeeBuckets[])
    {
        approximate(
                StateTree.headsUpRoot(),
                absDealerBuckets,
                absDealeeBuckets,
                1.0, 1.0);
    }

    private double approximate(
            StateTree.Node node,
            char           absDealerBuckets[],
            char           absDealeeBuckets[],
            double         pDealer,
            double         pDealee)
    {
        boolean  canRaise      = node.state().canRaise();
        boolean  dealerProp    = node.state().dealerIsNext();
        double   expectedValue = 0;
        double[] expectation   =
                new double[ AbstractAction.VALUES.length ];
        Arrays.fill(expectation, Double.NaN);

        InfoBranch infoBranch = INFO.info(
                node.pathToFlop(), node.round());

        char roundBucket =
                (dealerProp
                ? absDealerBuckets
                : absDealeeBuckets)[ node.round().ordinal() ];
        InfoSet info = infoBranch.get(roundBucket, node.roundPathId());

        double probabilities[] = info.probabilities(canRaise);
        Map<AbstractAction, Node> acts = node.acts();
        for (Map.Entry<AbstractAction, Node> next : acts.entrySet())
        {
            double actProb = probabilities[ next.getKey().ordinal() ];
            if (actProb == 0 /*&& info.isInformed()*/) continue;

            StateTree.Node nextNode = next.getValue();
            State          state    = nextNode.state();
            HeadsUpStatus  status   = state.headsUpStatus();
            if (status != HeadsUpStatus.IN_PROGRESS) {
                double dealerNonLossProb =
                        (status == HeadsUpStatus.SHOWDOWN)
                        ? ODDS.nonLossProb(
                                absDealerBuckets[3],
                                absDealeeBuckets[3])
                        : (status == HeadsUpStatus.DEALER_WINS)
                        ? 1 : 0;
                double toWin = dealerNonLossProb *
                                (dealerProp ? 1 : -1);
                return state.pot().smallBlinds() * toWin;
            }

            double val = approximate(
                            nextNode,
                            absDealerBuckets,
                            absDealeeBuckets,
                            pDealer * (dealerProp ? actProb : 1.0),
                            pDealee * (dealerProp ? 1.0 : actProb));
                         //* (dealerProp ? 1 : -1);

            expectation[ next.getKey().ordinal() ] = val;
            expectedValue += actProb * val;
        }

        double[] counterfactualRegret =
                new double[ AbstractAction.VALUES.length ];
        for (AbstractAction act : acts.keySet())
        {
            // todo: confirm that * pDealee makes sense
            double cRegret =
                    (expectation[ act.ordinal() ] - expectedValue) * pDealee;
            counterfactualRegret[ act.ordinal() ] = cRegret;
        }
        info.add(counterfactualRegret, canRaise);

        return (dealerProp ? 1 : -1) * expectedValue;
    }
}