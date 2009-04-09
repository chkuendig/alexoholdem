package ao.regret.holdem;

import ao.bucket.abstraction.access.odds.IBucketOdds;
import ao.holdem.engine.state.HeadsUpStatus;
import ao.holdem.engine.state.tree.StateTree;
import ao.holdem.engine.state.tree.StateTree.Node;
import ao.holdem.model.act.AbstractAction;
import ao.regret.holdem.InfoBranch.InfoSet;

import java.util.Map;

/**
 * User: shalom
 * Date: Feb 26, 2009
 * Time: 1:16:44 PM
 */
public class RegretMinimizer
{
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
        approximate(
                StateTree.headsUpRoot(),
                absDealerBuckets,
                absDealeeBuckets,
                1.0, 1.0);
    }


    //--------------------------------------------------------------------
    private double approximate(
            StateTree.Node node,
            char           absDealerBuckets[],
            char           absDealeeBuckets[],
            double         pDealer,
            double         pDealee)
    {
        boolean canRaise      = node.canRaise();
        boolean dealerProp    = node.dealerIsNext();
        double  expectedValue = 0;
        double  expectation[] = {0, 0, 0};

        InfoBranch infoBranch = INFO.info(
                node.pathToFlop(), node.round());

        char roundBucket =
                (dealerProp
                ? absDealerBuckets
                : absDealeeBuckets)[ node.round().ordinal() ];
        InfoSet info = infoBranch.get(roundBucket, node.roundPathId());

        double probabilities[] = info.probabilities(canRaise);
//        double probabilities[] = info.probabilities(
//                canRaise, node.canCheck(), fudge);
        Map<AbstractAction, Node> acts = node.acts();
        for (Map.Entry<AbstractAction, Node> next : acts.entrySet())
        {
            double actProb = probabilities[ next.getKey().ordinal() ];
//            if (actProb == 0) {
//                continue;
//            }

            double         val; // from POV of dealer
            StateTree.Node nextNode = next.getValue();
            HeadsUpStatus  status   = nextNode.status();
            if (status != HeadsUpStatus.IN_PROGRESS) {
                if (status == HeadsUpStatus.SHOWDOWN) {
                    val = nextNode.stakes() * (
                            ODDS.nonLossProb(
                                    absDealerBuckets[3],
                                    absDealeeBuckets[3]) - 0.5) * 2.0;
                } else {
                    if (status == HeadsUpStatus.DEALER_WINS) {
                        val =  nextNode.dealeeCommit();
                    } else {
                        val = -nextNode.dealerCommit();
                    }
                }
            } else {
                val = approximate(
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
}