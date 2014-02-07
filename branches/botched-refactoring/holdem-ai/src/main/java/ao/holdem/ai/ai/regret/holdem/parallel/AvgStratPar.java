package ao.holdem.ai.ai.regret.holdem.parallel;

import ao.holdem.engine.state.HeadsUpStatus;
import ao.holdem.ai.ai.regret.holdem.tree.StateTree;
import ao.holdem.model.act.AbstractAction;
import ao.holdem.ai.ai.regret.holdem.InfoMatrix;
import ao.holdem.ai.ai.regret.holdem.InfoPart;
import jsr166y.forkjoin.ForkJoinPool;
import jsr166y.forkjoin.RecursiveAction;

/**
 * User: alex
 * Date: 26-Apr-2009
 * Time: 11:02:33 PM
 */
public class AvgStratPar
        extends RecursiveAction
{
    //--------------------------------------------------------------------
    public static void iterate(
            ForkJoinPool exec,
            InfoPart     info,
            char         absDealerBuckets[],
            char         absDealeeBuckets[])
    {
        exec.invoke(new Starter(
                info, absDealerBuckets, absDealeeBuckets));
    }

    private static class Starter extends RecursiveAction
    {
        private final InfoPart INFO;
        private final char     DEALER_BUCKETS[];
        private final char     DEALEE_BUCKETS[];

        public Starter(
                InfoPart info,
                char     absDealerBuckets[],
                char     absDealeeBuckets[])
        {
            INFO           = info;
            DEALER_BUCKETS = absDealerBuckets;
            DEALEE_BUCKETS = absDealeeBuckets;
        }

        protected void compute() {
            forkJoin(new AvgStratPar(
                        INFO, DEALER_BUCKETS, DEALEE_BUCKETS,
                        true, StateTree.headsUpRoot(), 1.0),
                      new AvgStratPar(
                        INFO, DEALER_BUCKETS, DEALEE_BUCKETS,
                        false, StateTree.headsUpRoot(), 1.0));
        }
    }


    //--------------------------------------------------------------------
    private final InfoPart       INFO;
    private final char           DEALER_BUCKETS[];
    private final char           DEALEE_BUCKETS[];
    private final boolean        FOR_DEALER;
    private final StateTree.Node NODE;
    private final double         PROP_REACH;


    //--------------------------------------------------------------------
    private AvgStratPar(
            InfoPart       info,
            char           absDealerBuckets[],
            char           absDealeeBuckets[],
            boolean        forDealer,
            StateTree.Node node,
            double         propReach)
    {
        INFO           = info;
        DEALER_BUCKETS = absDealerBuckets;
        DEALEE_BUCKETS = absDealeeBuckets;
        FOR_DEALER     = forDealer;
        NODE           = node;
        PROP_REACH     = propReach;
    }


    //--------------------------------------------------------------------
    protected void compute() {

//        if (NODE.round().ordinal() <= Round.PREFLOP.ordinal()) {
            updateOrPassStrategy(NODE, PROP_REACH);
//        } else {
//            updateOrPassStrategyPar();
//        }
    }


    //--------------------------------------------------------------------
    private void updateOrPassStrategy(
            StateTree.Node node,
            double         propReach)
    {
        if (node.status() != HeadsUpStatus.IN_PROGRESS) return;

        if (node.dealerIsNext() == FOR_DEALER) {
            updateStrategy(node, propReach);
        } else {
            passStrategy  (node, propReach);
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

            updateOrPassStrategy(
                    nextNode, propReach * actProb);
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
    private void updateOrPassStrategyPar()
    {
        if (NODE.status() != HeadsUpStatus.IN_PROGRESS) return;

        if (NODE.dealerIsNext() == FOR_DEALER) {
            updateStrategyPar();
        } else {
            passStrategyPar  ();
        }
    }

    private void updateStrategyPar()
    {
        InfoMatrix.InfoSet info       = infoSet(NODE);
        double             strategy[] = info.strategy();

        AvgStratPar callAct = null, raiseAct = null;
        for (AbstractAction act : AbstractAction.VALUES) {
            StateTree.Node nextNode = NODE.kid(act);
            if (nextNode == null) continue;

            double actProb = strategy[ act.ordinal() ];
            if (actProb == 0) continue;

            if (act == AbstractAction.CHECK_CALL) {
                callAct = new AvgStratPar(
                        INFO, DEALER_BUCKETS, DEALEE_BUCKETS,
                        FOR_DEALER, nextNode, PROP_REACH * actProb);
            } else if (act == AbstractAction.BET_RAISE) {
                raiseAct = new AvgStratPar(
                        INFO, DEALER_BUCKETS, DEALEE_BUCKETS,
                        FOR_DEALER, nextNode, PROP_REACH * actProb);
            } else {
                updateOrPassStrategy(
                    nextNode, PROP_REACH * actProb);
            }
        }

        if (callAct != null && raiseAct != null) {
            forkJoin(callAct, raiseAct);
        } else if (callAct != null) {
            callAct.exec();
//            invokeAll(callAct);
        } else if (raiseAct != null) {
            raiseAct.exec();
//            invokeAll(raiseAct);
        }

        info.add(strategy, PROP_REACH);
    }

    private void passStrategyPar()
    {
        AvgStratPar callAct = null, raiseAct = null;
        for (AbstractAction act : AbstractAction.VALUES) {
            StateTree.Node nextNode = NODE.kid(act);
            if (nextNode == null) continue;

            if (act == AbstractAction.CHECK_CALL) {
                callAct = new AvgStratPar(
                        INFO, DEALER_BUCKETS, DEALEE_BUCKETS,
                        FOR_DEALER, nextNode, PROP_REACH);
            } else if (act == AbstractAction.BET_RAISE) {
                raiseAct = new AvgStratPar(
                        INFO, DEALER_BUCKETS, DEALEE_BUCKETS,
                        FOR_DEALER, nextNode, PROP_REACH);
            } else {
                updateOrPassStrategy(
                    nextNode, PROP_REACH);
            }
        }

        if (callAct != null && raiseAct != null) {
            forkJoin(callAct, raiseAct);
        } else if (callAct != null) {
            callAct.exec();
//            invokeAll(callAct);
        } else if (raiseAct != null) {
            raiseAct.exec();
//            invokeAll(raiseAct);
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