package ao.general.impl;

import ao.util.data.tuple.TwoTuple;

/**
 * Date: 7/9/11
 * Time: 5:15 PM
 */
public class CounterfactualRegretMinimizer
        implements NashEquilibriumStrategySolver
{
    //------------------------------------------------------------------------
    @Override
    public void runSimulation(
            /*FiniteZeroSumExtensiveGame game,
            StrategyProfile            strategy*/
            TwoTuple<int[], int[]> bothPlayerJointBucketSequenceSample,
            GameStateTree          stateTree,
            StrategyProfile        strategy)
    {
//        TwoTuple<int[], int[]> bothPlayerJointBucketSequenceSample =
//                game.bothPlayerJointBucketSequenceSample();

//        GameStateTree stateTree =
//                new GameStateTree( game.initialState() );

        minimizeRegret(
                stateTree.initialStateIndex(),
                stateTree,
                strategy,
                bothPlayerJointBucketSequenceSample.first(),
                bothPlayerJointBucketSequenceSample.second(),
                1.0, 1.0);
    }


    //------------------------------------------------------------------------
    private double minimizeRegret(
            int             stateIndex,
            GameStateTree   stateTree,
            StrategyProfile strategy,
            int[]           ftaJointBucketSequence,
            int[]           ltaJointBucketSequence,
            double          ftaProbability,
            double          ltaProbability)
    {
        boolean firstPlayerNext =
                stateTree.isFirstPlayerNextToAct( stateIndex );

        int[] jointBucketSequence =
                firstPlayerNext
                ? ftaJointBucketSequence
                : ltaJointBucketSequence;

        InformationSet infoSet = strategy.informationSet(
                stateIndex,
                stateTree.bucketRound(stateIndex),
                jointBucketSequence);

        double[] probabilities         =
                infoSet.positiveRegretStrategy(
                        stateTree.childCount( stateIndex) );
        double[] utilities             = new double[ probabilities.length ];
        double   counterfactualUtility = 0;

        for (int act = 0; act < probabilities.length; act++)
        {
            double actProb = probabilities[ act ];

            int nextIndex = stateTree.stateIndexOfChild(
                    stateIndex, act);

            double value;
            if (stateTree.isTerminal( nextIndex ))
            {
                value = stateTree.outcomeValue(
                            nextIndex,
                            ftaJointBucketSequence,
                            ltaJointBucketSequence,
                            firstPlayerNext);
            }
            else
            {
                double ftaNextProbability = firstPlayerNext
                        ? actProb * ftaProbability : ftaProbability;

                double ltaNextProbability = firstPlayerNext
                        ? ltaProbability : actProb * ltaProbability;

                value = minimizeRegret(
                            nextIndex,
                            stateTree,
                            strategy,
                            ftaJointBucketSequence,
                            ltaJointBucketSequence,
                            ftaNextProbability,
                            ltaNextProbability);
            }

            utilities[ act ] = value;
            counterfactualUtility += value * actProb;
        }

        double oppReachingFactor =
                firstPlayerNext
                ? ltaProbability
                : ftaProbability;
        double immediateCounterfactualRegret[] =
                new double[ probabilities.length ];

        for (int act = 0; act < probabilities.length; act++)
        {
            double counterfactualRegret =
                    (utilities[ act ] - counterfactualUtility)
                        * oppReachingFactor;

            immediateCounterfactualRegret[ act ] = counterfactualRegret;
        }

        double reachProbability =
                firstPlayerNext
                ? ftaProbability
                : ltaProbability;

        infoSet.add(
                immediateCounterfactualRegret,
                probabilities,
                reachProbability);

        return counterfactualUtility;
    }
}
