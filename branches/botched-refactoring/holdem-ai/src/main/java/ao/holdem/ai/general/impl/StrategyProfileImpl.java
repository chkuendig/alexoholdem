package ao.holdem.ai.general.impl;

import ao.holdem.ai.general.InformationSet;
import ao.holdem.ai.general.StrategyProfile;
import ao.util.data.AutovivifiedList;
import ao.util.math.rand.Rand;
import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.List;

/**
 * Date: 7/10/11
 * Time: 9:15 PM
 */
public class StrategyProfileImpl
        implements StrategyProfile
{
    //------------------------------------------------------------------------
    private final List<List<InfoSet>> actionToBucketToInfoSet;


    //------------------------------------------------------------------------
    public StrategyProfileImpl()
    {
        actionToBucketToInfoSet =
//                new ArrayList<List<InfoSet>>();
                new AutovivifiedList<List<InfoSet>>();
    }


    //------------------------------------------------------------------------
    @Override
    public InformationSet informationSet(
            int   actionSequence,
            int   bucketRound,
            int[] jointBucketSequence)
    {
        Preconditions.checkArgument(
                bucketRound == 0);
        Preconditions.checkArgument(
                jointBucketSequence.length == 1);

        List<InfoSet> bucketToInfoSet =
                actionToBucketToInfoSet.get( actionSequence );
        if (bucketToInfoSet == null)
        {
            bucketToInfoSet = new AutovivifiedList<InfoSet>();
            actionToBucketToInfoSet.set(
                    actionSequence, bucketToInfoSet);
        }

        int bucket = jointBucketSequence[ bucketRound ];
        InfoSet infoSet = bucketToInfoSet.get( bucket );
        if (infoSet == null)
        {
            infoSet = new InfoSet();
            bucketToInfoSet.set(bucket, infoSet);
        }
        return infoSet;
    }


    //------------------------------------------------------------------------
    private class InfoSet
            implements InformationSet
    {
        //--------------------------------------------------------------------
        private double[] regrets;
        private double[] sums;
        private double[] reachSums;


        //--------------------------------------------------------------------
        @Override
        public double[] positiveRegretStrategy(int actionCount)
        {
            if (regrets == null)
            {
                regrets   = new double[ actionCount ];
                sums      = new double[ actionCount ];
                reachSums = new double[ actionCount ];
            }

            double prob[]    = new double[ actionCount ];
            double cumRegret = positiveCumulativeCounterfactualRegret();

            if (cumRegret <= 0)
            {
                for (int i = 0; i < actionCount; i++)
                {
                    prob[ i ] = 1.0 / actionCount;
                }
            }
            else
            {
                for (int i = 0; i < actionCount; i++)
                {
                    prob[ i ] = Math.max(0,
                            regrets[ i ] / cumRegret);
                }
            }

            return prob;
        }

        private double positiveCumulativeCounterfactualRegret() {
            double sum = 0;
            for (double regret : regrets) {
                sum += Math.max(regret, 0);
            }
            return sum;
        }


        //--------------------------------------------------------------------
        @Override
        public void add(
                double[] immediateCounterfactualRegret,
                double[] probabilities,
                double   reachProbability)
        {
            for (int i = 0; i < immediateCounterfactualRegret.length; i++)
            {
                sums     [ i ] += probabilities[ i ] * reachProbability;
                reachSums[ i ] += reachProbability;
                regrets  [ i ] += immediateCounterfactualRegret[ i ];
            }
        }


        //--------------------------------------------------------------------
        public int nextProbableAction()
        {
            double[] prob = averageStrategy();

            double coinFlop = Rand.nextDouble();
            for (int i = 0; i < prob.length; i++)
            {
                if (prob[ i ] < coinFlop) {
                    return i;
                }

                coinFlop -= prob[ i ];
            }

            throw new Error(Arrays.toString( prob ));
        }

        public double[] averageStrategy()
        {
            double[] probs = new double[ regrets.length ];
            for (int i = 0; i < probs.length; i++)
            {
                probs[ i ] = sums[ i ] / reachSums[ i ];
            }
            return probs;
        }


        //----------------------------------------------------------------
        @Override public String toString() {
            return Arrays.toString(averageStrategy());
        }
    }
}
