package ao.holdem.ai.general;

/**
 * Date: 7/9/11
 * Time: 4:41 PM
 */
public interface StrategyProfile
{
    //------------------------------------------------------------------------
    InformationSet informationSet(
            int   actionSequence,
            int   bucketRound,
            int[] jointBucketSequence);
}
