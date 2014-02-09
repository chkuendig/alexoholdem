package ao.holdem.bot.regret.serial;

import ao.holdem.engine.state.HeadsUpStatus;
import ao.holdem.engine.state.tree.StateTree;
import ao.holdem.model.act.AbstractAction;
import ao.holdem.bot.regret.InfoMatrix;
import ao.holdem.bot.regret.InfoPart;
import ao.holdem.bot.regret.IterativeMinimizer;

/**
 * User: alex
 * Date: 26-Apr-2009
 * Time: 11:02:33 PM
 */
public class AvgStrat implements IterativeMinimizer
{
    //--------------------------------------------------------------------
//    private final ExecutorService EXEC;

    private final InfoPart INFO;
    private       char     DEALER_BUCKETS[];
    private       char     DEALEE_BUCKETS[];


    //--------------------------------------------------------------------
    public AvgStrat(
                InfoPart        info
//            ,   ExecutorService exec
            )
    {
        INFO = info;
//        EXEC = exec;
    }


    //--------------------------------------------------------------------
    public void iterate(
            char absDealerBuckets[],
            char absDealeeBuckets[])
    {
        DEALER_BUCKETS = absDealerBuckets;
        DEALEE_BUCKETS = absDealeeBuckets;

        updateOrPassStrategy(
                true, StateTree.headsUpRoot(), 1.0);

        updateOrPassStrategy(
                false, StateTree.headsUpRoot(), 1.0);

//        try {
//            doIterate();
//        } catch (InterruptedException e) {
//            throw new Error( e );
//        }
    }

//    private void doIterate() throws InterruptedException
//    {
//        Collection<Callable<Void>> todo =
//                new ArrayList<Callable<Void>>(2);
//
//        todo.add(new Callable<Void>() {
//                    public Void call() throws Exception {
//                        updateOrPassStrategy(
//                                true, StateTree.headsUpRoot(), 1.0);
//                        return null;
//                    }});
//        todo.add(new Callable<Void>() {
//                    public Void call() throws Exception {
//                        updateOrPassStrategy(
//                                false, StateTree.headsUpRoot(), 1.0);
//                        return null;
//                    }});
//
//        EXEC.invokeAll(todo);
//    }


    //--------------------------------------------------------------------
    private void updateOrPassStrategy(
            boolean        forDealer,
            StateTree.Node node,
            double         propReach)
    {
        if (node.status() != HeadsUpStatus.IN_PROGRESS) return;

        if (node.dealerIsNext() == forDealer) {
            updateStrategy(forDealer, node, propReach);
        } else {
            passStrategy(forDealer, node, propReach);
        }
    }

    private void updateStrategy(
            boolean        forDealer,
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

            updateOrPassStrategy(
                    forDealer, nextNode, propReach * actProb);
        }

        info.add(strategy, propReach);
    }

    private void passStrategy(
            boolean        forDealer,
            StateTree.Node node,
            double         propReach)
    {
        for (AbstractAction act : AbstractAction.VALUES) {
            StateTree.Node nextNode = node.kid(act);
            if (nextNode == null) continue;

            updateOrPassStrategy(forDealer, nextNode, propReach);
        }
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
