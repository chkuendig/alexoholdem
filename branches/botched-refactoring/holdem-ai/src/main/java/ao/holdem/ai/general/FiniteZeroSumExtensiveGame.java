package ao.holdem.ai.general;

import ao.util.data.tuple.TwoTuple;

/**
 * Date: 7/9/11
 * Time: 4:37 PM
 */
public interface FiniteZeroSumExtensiveGame
{
    //------------------------------------------------------------------------
    /**
     * @return joint bucket sequence for first/last to act respectively
     */
    TwoTuple<int[], int[]> bothPlayerJointBucketSequenceSample();

    GameState initialState();
}
