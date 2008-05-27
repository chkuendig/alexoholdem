package ao.regret.khun;

import ao.regret.khun.node.*;
import ao.simple.kuhn.KuhnAction;

import java.util.EnumMap;
import java.util.Map;

/**
 *
 */
public class Equalibrium
{
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
            return handleProponent(rA, rB, b, pA, pB, true);
//            return handleFirstProponent(rA, rB, b, pA, pB);
        }
        else if (rB instanceof ProponentNode)
        {
            return handleProponent(rA, rB, b, pA, pB, false);
//            return handleLastProponent(rA, rB, b, pA, pB);
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
    private double[] handleProponent(
            InfoNode            rA,
            InfoNode            rB,
            JointBucketSequence b,
            double              pA,
            double              pB,
            boolean             first)
    {
        ProponentNode proponent = (ProponentNode) (first ? rA : rB);
        OpponentNode  opponent  = (OpponentNode)  (first ? rB : rA);

        double                  expectedValue = 0;
        Map<KuhnAction, Double> expectation   =
                new EnumMap<KuhnAction, Double>(KuhnAction.class);
        for (KuhnAction act : KuhnAction.VALUES)
        {
            double actProb = proponent.probabilityOf(act);
            if (actProb == 0 && !proponent.isSparse())
            {
                expectation.put(act, 0.0);
                continue;
            }

            double val =
                approximate(
                        (first ? proponent : opponent ).child(act),
                        (first ? opponent  : proponent).child(act),
                        b,
                        pA * (first ? actProb : 1.0),
                        pB * (first ? 1.0 : actProb))
                [ first ? 0 : 1 ];

            expectation.put(act, val);
            expectedValue += actProb * val;
        }

        Map<KuhnAction, Double> counterfactualRegret  =
                new EnumMap<KuhnAction, Double>(KuhnAction.class);
        for (KuhnAction act : KuhnAction.VALUES)
        {
            double cRegret =
                    (expectation.get(act) - expectedValue) * pB;
            counterfactualRegret.put(act, cRegret);
        }
        proponent.add( counterfactualRegret );
        proponent.updateActionPabilities();

        return new double[]{
                (first ?  1 : -1) * expectedValue,
                (first ? -1 :  1) * expectedValue };
    }
}
