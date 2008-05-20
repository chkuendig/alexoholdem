package ao.regret;

import ao.regret.node.*;
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
            return handleFirstProponent(rA, rB, b, pA, pB);
        }
        else if (rB instanceof ProponentNode)
        {
            return handleLastProponent(rA, rB, b, pA, pB);
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
    private double[] handleFirstProponent(
            InfoNode            rA,
            InfoNode            rB,
            JointBucketSequence b,
            double              pA,
            double              pB)
    {
        ProponentNode proponent = (ProponentNode) rA;
        OpponentNode  opponent  = (OpponentNode) rB;

        double                  expectedValue = 0;
        Map<KuhnAction, Double> expectation   =
                new EnumMap<KuhnAction, Double>(KuhnAction.class);
        for (KuhnAction act : KuhnAction.VALUES)
        {
            double val =
                    approximate(proponent.child(act),
                                opponent.child(act),
                                b,
                                pA * proponent.probabilityOf(act),
                                pB)[0];
                    //proponent.expectedValue(act, opponent);

            expectation.put(act, val);
            expectedValue += proponent.probabilityOf(act) * val;
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

        return new double[]{ expectedValue, -expectedValue };
    }

    private double[] handleLastProponent(
            InfoNode            rA,
            InfoNode            rB,
            JointBucketSequence b,
            double              pA,
            double              pB)
    {
        OpponentNode  opponent  = (OpponentNode)  rA;
        ProponentNode proponent = (ProponentNode) rB;

        double                  expectedValue = 0;
        Map<KuhnAction, Double> expectation   =
                new EnumMap<KuhnAction, Double>(KuhnAction.class);
        for (KuhnAction act : KuhnAction.VALUES)
        {
            double val =
                    approximate(opponent.child(act),
                                proponent.child(act),
                                b,
                                pA,
                                pB * proponent.probabilityOf(act))[1];

            expectation.put(act, val);
            expectedValue += proponent.probabilityOf(act) * val;
        }

        Map<KuhnAction, Double> counterfactualRegret  =
                new EnumMap<KuhnAction, Double>(KuhnAction.class);
        for (KuhnAction act : KuhnAction.VALUES)
        {
            double cRegret =
                    (expectation.get(act) - expectedValue) * pA;
            counterfactualRegret.put(act, cRegret);
        }
        proponent.add( counterfactualRegret );
        proponent.updateActionPabilities();

        return new double[]{ -expectedValue, expectedValue };
    }
}
