package ao.holdem.ai.ai.regret.alexo.pair;

import ao.holdem.ai.ai.regret.alexo.JointBucketSequence;

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
