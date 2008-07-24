package ao.regret.alexo.pair;

import ao.regret.alexo.JointBucketSequence;

/**
 *
 */
public interface InfoPair
{
    public double approximate(
            JointBucketSequence b,
            double              pA,
            double              pB,
            double              aggression);
}
