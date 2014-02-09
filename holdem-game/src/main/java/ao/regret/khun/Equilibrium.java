package ao.regret.khun;

import ao.regret.InfoNode;
import ao.regret.khun.node.*;
import ao.simple.kuhn.KuhnAction;
import ao.simple.kuhn.KuhnCard;
import ao.simple.kuhn.state.KuhnState;
import ao.simple.kuhn.state.StateFlow;

import java.util.EnumMap;
import java.util.Map;

/**
 *
 */
public class Equilibrium
{
    //--------------------------------------------------------------------
    public void minimizeRegret(
            KuhnInfoTree infoTree,
            KuhnCard     buckets[])
    {
        minimizeRegret(
                KuhnState.FIRST_ACTION,
                infoTree,
                buckets,
                1.0, 1.0);
    }
    private double minimizeRegret(
            KuhnState    state,
            KuhnInfoTree infoTree,
            KuhnCard     buckets[],
            double       pA,
            double       pB)
    {
        boolean  firstToAct = state.firstToAct();
        KuhnCard bucket     = buckets[firstToAct ? 0 : 1];

        KuhnInfoTree.InfoSet info =
                infoTree.infoSet(bucket, state);

        double utilities[]           = {0, 0, 0};
        double counterfactualUtility = 0;

        double actionProbabilities[] = info.positiveRegretStrategy();
        for (KuhnAction act : KuhnAction.VALUES) {
            double actProb = actionProbabilities[ act.ordinal() ];

            double val;
            StateFlow next = state.advance(act);
            if (next.endOfHand()) {
                val = TerminalNode.vs(
                        buckets[0], buckets[1], next.outcome());
            } else {
                val = minimizeRegret(
                        next.state(),
                        infoTree,
                        buckets,
                        (firstToAct ? pA * actProb : pA),
                        (firstToAct ? pB : pB * actProb));
            }

            utilities[ act.ordinal() ] = val;
            counterfactualUtility += val * actProb;
        }

        double oppReachingFactor = (firstToAct ? pB : -pA);
        double immediateCounterfactualRegret[] =
                new double[ KuhnAction.VALUES.length ];
        for (KuhnAction act : KuhnAction.VALUES)
        {
            double cRegret =
                    (utilities[ act.ordinal() ] - counterfactualUtility)
                        * oppReachingFactor;

            immediateCounterfactualRegret[ act.ordinal() ] = cRegret;

        }
        info.add(immediateCounterfactualRegret,
                 actionProbabilities,
                 firstToAct ? pA : pB);

        return counterfactualUtility;
    }



    //--------------------------------------------------------------------
    /**
     * @param rA an information set tree for player 1.
     * @param rB an information set tree for player 2.
     * @param b A joint bucket sequence.
     * @param pA probability of player 1 playing to reach the node.
     * @param pB probability of player 2 playing to reach the node.
     * @return expected value for first and last players
     */
    public double[] approximate(InfoNode            rA,
                                InfoNode            rB,
                                JointBucketSequence b,
                                double              pA,
                                double              pB)
    {
        if (rA instanceof ProponentNode)
        {
            return handlePlayer(rA, rB, b, pA, pB, true);
        }
        else if (rB instanceof ProponentNode)
        {
            return handlePlayer(rA, rB, b, pA, pB, false);
        }
        else if (rA instanceof BucketNode)
        {
            BucketNode bucketA = (BucketNode) rA;
            BucketNode bucketB = (BucketNode) rB;

            PlayerNode nextA = bucketA.accordingTo(b);
            PlayerNode nextB = bucketB.accordingTo(b);

            return approximate(nextA, nextB, b, pA, pB);
        }
        else if (rA instanceof TerminalNode)
        {
            TerminalNode terminalA = (TerminalNode) rA;
            TerminalNode terminalB = (TerminalNode) rB;

            double utility = terminalA.expectedValue( terminalB );
            return new double[]{ utility, -utility };
        }
        else
        {
            throw new Error();
        }
    }


    //--------------------------------------------------------------------
    private double[] handlePlayer(
            InfoNode            rA,
            InfoNode            rB,
            JointBucketSequence b,
            double              pA,
            double              pB,
            boolean             first)
    {
        ProponentNode proponent = (ProponentNode) (first ? rA : rB);

        double                  expectedValue = 0;
        Map<KuhnAction, Double> expectation   =
                new EnumMap<KuhnAction, Double>(KuhnAction.class);
        for (KuhnAction act : KuhnAction.VALUES)
        {
            double actProb = proponent.probabilityOf(act);
            double val =
                approximate(
                        ((PlayerNode) rA).child(act),
                        ((PlayerNode) rB).child(act),
                        b,
                        pA * (first ? actProb : 1.0),
                        pB * (first ? 1.0 : actProb))
                [ first ? 0 : 1 ];

            expectation.put(act, val);
            expectedValue += actProb * val;
        }

        double proReachProb = (first ? pA : pB);
        double oppReachProb = (first ? pB : pA);

        Map<KuhnAction, Double> counterfactualRegret  =
                new EnumMap<KuhnAction, Double>(KuhnAction.class);
        for (KuhnAction act : KuhnAction.VALUES)
        {
            double cRegret =
                    (expectation.get(act) - expectedValue) *
                        oppReachProb;
            counterfactualRegret.put(act, cRegret);
        }
        proponent.add(
                counterfactualRegret,
                proReachProb);
//        proponent.updateActionPabilities();

        return new double[]{
                (first ?  1 : -1) * expectedValue,
                (first ? -1 :  1) * expectedValue };
    }


    //--------------------------------------------------------------------
    private double[] approximate(InfoNode            rA,
                                 InfoNode            rB,
                                 JointBucketSequence b)
    {
        if (rA instanceof ProponentNode)
        {
            return handlePlayer(rA, rB, b, true);
        }
        else if (rB instanceof ProponentNode)
        {
            return handlePlayer(rA, rB, b, false);
        }
        else if (rA instanceof BucketNode)
        {
            BucketNode bucketA = (BucketNode) rA;
            BucketNode bucketB = (BucketNode) rB;

            PlayerNode nextA = bucketA.accordingTo(b);
            PlayerNode nextB = bucketB.accordingTo(b);

            return approximate(nextA, nextB, b);
        }
        else if (rA instanceof TerminalNode)
        {
            TerminalNode terminalA = (TerminalNode) rA;
            TerminalNode terminalB = (TerminalNode) rB;

            double utility = terminalA.expectedValue( terminalB );
            return new double[]{ utility, -utility };
        }
        else
        {
            throw new Error();
        }
    }

    private double[] handlePlayer(
            InfoNode            rA,
            InfoNode            rB,
            JointBucketSequence b,
            boolean             first)
    {
        ProponentNode proponent = (ProponentNode) (first ? rA : rB);
        double        expectedValue = 0;
        for (KuhnAction act : KuhnAction.VALUES)
        {
            double actProb = proponent.probabilityOf(act);
            double val =
                    approximate(
                            ((PlayerNode) rA).child(act),
                            ((PlayerNode) rB).child(act),
                            b)
                            [ first ? 0 : 1 ];

            expectedValue += actProb * val;
        }

        return new double[]{
                (first ?  1 : -1) * expectedValue,
                (first ? -1 :  1) * expectedValue };
    }
}
