package ao.regret.holdem;

import ao.bucket.abstraction.access.odds.BucketOdds;
import ao.holdem.engine.state.HeadsUpStatus;
import ao.holdem.engine.state.State;
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


    //--------------------------------------------------------------------
    private Expectation approximate(
            StateTree.Node node,
            char           absDealerBuckets[],
            char           absDealeeBuckets[],
            double         pDealer,
            double         pDealee)
    {
        boolean       canRaise      = node.state().canRaise();
        boolean       dealerProp    = node.state().dealerIsNext();
        Expectation   expectedValue = new Expectation();
        Expectation[] expectation   =
                new Expectation[ AbstractAction.VALUES.length ];
        //Arrays.fill(expectation, Double.NaN);

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
            if (actProb == 0) {
                expectation[ next.getKey().ordinal() ] =
                        new Expectation();
                continue;
            }

            Expectation    val;
            StateTree.Node nextNode = next.getValue();
            State          state    = nextNode.state();
            HeadsUpStatus  status   = state.headsUpStatus();
            if (status != HeadsUpStatus.IN_PROGRESS) {
                if (status == HeadsUpStatus.SHOWDOWN) {
                    val = new Expectation(state.stakes().smallBets(),
                                          ODDS.nonLossProb(
                                                 absDealerBuckets[3],
                                                 absDealeeBuckets[3]));
                } else {
                    int dealeeCommit = state.seats(0).commitment().smallBlinds();
                    int dealerCommit = state.seats(1).commitment().smallBlinds();

                    val = new Expectation(dealerCommit, dealeeCommit,
                                          (status == HeadsUpStatus.DEALER_WINS));
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
            expectedValue = expectedValue.plus(val.temper(actProb));
        }

        double[] counterfactualRegret =
                new double[ AbstractAction.VALUES.length ];
        for (AbstractAction act : acts.keySet())
        {
            double cRegret =
                    (expectation[ act.ordinal() ].value(dealerProp) -
                                    expectedValue.value(dealerProp))
                            * (dealerProp ? pDealee : pDealer);
            counterfactualRegret[ act.ordinal() ] = cRegret;
        }
        info.add(counterfactualRegret, canRaise);

        return expectedValue;
    }
}