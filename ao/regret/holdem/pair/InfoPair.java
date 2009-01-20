package ao.regret.holdem.pair;

import ao.regret.holdem.JointBucketSequence;


/**
 * Date: Jan 18, 2009
 * Time: 7:05:55 PM
 */
public interface InfoPair
{
    public double approximate(
            JointBucketSequence b,
            double              pDealer,
            double              pDealee,
            double              aggression);
}
