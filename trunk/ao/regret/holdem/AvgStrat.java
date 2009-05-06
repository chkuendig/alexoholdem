package ao.regret.holdem;

import ao.holdem.engine.state.HeadsUpStatus;
import ao.holdem.engine.state.tree.StateTree;
import ao.holdem.model.act.AbstractAction;

/**
 * User: alex
 * Date: 26-Apr-2009
 * Time: 11:02:33 PM
 */
public class AvgStrat
{
    //--------------------------------------------------------------------
    private final InfoPart INFO;
    private       char     DEALER_BUCKETS[];
    private       char     DEALEE_BUCKETS[];

    private       boolean  forDealer;


    //--------------------------------------------------------------------
    public AvgStrat(InfoPart info)
    {
        INFO = info;
    }


    //--------------------------------------------------------------------
    public void iterate(
            char absDealerBuckets[],
            char absDealeeBuckets[])
    {
        DEALER_BUCKETS = absDealerBuckets;
        DEALEE_BUCKETS = absDealeeBuckets;

        forDealer = true;
        updateOrPassStrategy(StateTree.headsUpRoot(), 1.0);

        forDealer = false;
        updateOrPassStrategy(StateTree.headsUpRoot(), 1.0);
    }


    //--------------------------------------------------------------------
    private void updateOrPassStrategy(
            StateTree.Node node,
            double         propReach)
    {
        if (node.status() != HeadsUpStatus.IN_PROGRESS) return;

        if (node.dealerIsNext() == forDealer) {
            updateStrategy(node, propReach);
        } else {
            passStrategy(node, propReach);
        }
    }

    private void updateStrategy(
            StateTree.Node node,
            double         propReach)
    {
        InfoMatrix.InfoSet info       = infoSet(node);
        double             strategy[] = info.strategy();

        for (AbstractAction act : AbstractAction.VALUES) {
            StateTree.Node nextNode = node.kid(act);
            if (nextNode == null) continue;

            double actProb = strategy[ act.ordinal() ];
            if (actProb == 0) continue;

            updateOrPassStrategy(nextNode, propReach * actProb);
        }

        info.add(strategy, propReach);
    }

    private void passStrategy(
            StateTree.Node node,
            double         propReach)
    {
        for (AbstractAction act : AbstractAction.VALUES) {
            StateTree.Node nextNode = node.kid(act);
            if (nextNode == null) continue;

            updateOrPassStrategy(nextNode, propReach);
        }
    }


    //--------------------------------------------------------------------
    private InfoMatrix.InfoSet infoSet(StateTree.Node node)
    {
        char roundBucket =
                (node.dealerIsNext()
                 ? DEALER_BUCKETS
                 : DEALEE_BUCKETS)[ node.round().ordinal() ];
        return node.infoSet(roundBucket, INFO);
    }
}
