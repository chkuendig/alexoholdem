package ao.regret.holdem.pair;

import ao.regret.holdem.JointBucketSequence;
import ao.regret.holdem.node.TerminalNode;

/**
 * Date: Jan 18, 2009
 * Time: 7:11:13 PM
 */
public class TerminalPair implements InfoPair
{
    //--------------------------------------------------------------------
    private final TerminalNode DEALER;
    private final TerminalNode DEALEE;


    //--------------------------------------------------------------------
    public TerminalPair(TerminalNode dealer,
                        TerminalNode dealee)
    {
        DEALER = dealer;
        DEALEE = dealee;
    }


    //--------------------------------------------------------------------
    public double approximate(
            JointBucketSequence b,
            double pDealer, double pDealee, double aggression)
    {
        return dealerExpectation() * (1.0 + aggression);
    }


    //--------------------------------------------------------------------
    public double dealerExpectation()
    {
        return DEALER.expectedValue(DEALEE);
    }
}
