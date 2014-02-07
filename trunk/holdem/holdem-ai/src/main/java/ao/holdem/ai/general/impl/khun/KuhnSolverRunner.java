package ao.holdem.ai.general.impl.khun;

import ao.holdem.ai.general.FiniteZeroSumExtensiveGame;
import ao.holdem.ai.general.NashEquilibriumStrategySolver;
import ao.holdem.ai.general.StrategyProfile;
import ao.holdem.ai.general.impl.CounterfactualRegretMinimizer;
import ao.holdem.ai.general.impl.GameStateTree;
import ao.holdem.ai.general.impl.StrategyProfileImpl;

/**
 * Date: 7/10/11
 * Time: 11:27 PM
 */
public class KuhnSolverRunner
{
    //------------------------------------------------------------------------
    public static void main(String[] args)
    {
        FiniteZeroSumExtensiveGame game =
                new KuhnPoker();

        NashEquilibriumStrategySolver solver =
                new CounterfactualRegretMinimizer();

        GameStateTree stateTree =
                new GameStateTree( game.initialState() );

        StrategyProfile strategyProfile =
                new StrategyProfileImpl();

        for (int i = 0; i < 10000; i++)
        {
            solver.runSimulation(
                    game.bothPlayerJointBucketSequenceSample(),
                    stateTree,
                    strategyProfile);
        }
    }
}
