package ao.holdem.ai.ai.regret.alexo.pair;

import ao.holdem.ai.ai.regret.alexo.JointBucketSequence;
import ao.holdem.ai.ai.regret.alexo.node.TerminalNode;

/**
 * 
 */
public class TerminalPair implements InfoPair
{
    //--------------------------------------------------------------------
    private final TerminalNode FIRST;
    private final TerminalNode LAST;


    //--------------------------------------------------------------------
    public TerminalPair(TerminalNode first,
                        TerminalNode last)
    {
        FIRST = first;
        LAST  = last;
    }


    //--------------------------------------------------------------------
    public double approximate(
            JointBucketSequence b,
            double pA, double pB, double aggression)
    {
        return firstToActExpectation() * (1.0 + aggression);
    }


    //--------------------------------------------------------------------
    private double firstToActExpectation()
    {
        return FIRST.expectedValue(LAST);
    }
}
