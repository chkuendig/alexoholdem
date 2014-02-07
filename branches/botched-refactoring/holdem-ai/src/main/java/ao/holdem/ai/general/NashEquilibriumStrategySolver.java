package ao.holdem.ai.general;

import ao.holdem.ai.general.impl.GameStateTree;
import ao.util.data.tuple.TwoTuple;

/**
 * Date: 7/9/11
 * Time: 4:39 PM
 */
public interface NashEquilibriumStrategySolver
{
    //------------------------------------------------------------------------
    void runSimulation(
//            FiniteZeroSumExtensiveGame game,
            TwoTuple<int[], int[]> bothPlayerJointBucketSequenceSample,
            GameStateTree          stateTree,
            StrategyProfile        strategy);
}
