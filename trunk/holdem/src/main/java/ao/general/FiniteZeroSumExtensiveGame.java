package ao.general;

import ao.util.data.tuple.TwoTuple;
import ca.ualberta.cs.poker.free.tournament.Pair;

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
