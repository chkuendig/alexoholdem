package ao.holdem.ai.general.impl.khun;


import ao.holdem.ai.ai.simple.kuhn.KuhnCard;
import ao.holdem.ai.general.FiniteZeroSumExtensiveGame;
import ao.holdem.ai.general.GameState;
import ao.util.data.tuple.TwoTuple;
import ao.util.math.stats.Permuter;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 7/9/11
 * Time: 10:14 PM
 */
public class KuhnPoker
        implements FiniteZeroSumExtensiveGame
{
    //------------------------------------------------------------------------
    private static final List<TwoTuple<int[], int[]>> hands =
            generateHands();

    private static List<TwoTuple<int[], int[]>> generateHands()
    {
        List<TwoTuple<int[], int[]>> hands =
                new ArrayList<TwoTuple<int[], int[]>>();

        for (KuhnCard[] permutation :
                new Permuter<KuhnCard>(KuhnCard.values(), 2))
        {
            hands.add(new TwoTuple<int[], int[]>(
                    new int[]{ permutation[0].ordinal() },
                    new int[]{ permutation[1].ordinal() }));
        }

        return hands;
    }


    //------------------------------------------------------------------------
    private int nextHandIndex = 0;


    //------------------------------------------------------------------------
    @Override
    public TwoTuple<int[], int[]> bothPlayerJointBucketSequenceSample()
    {
        TwoTuple<int[], int[]> sample =
                hands.get( nextHandIndex );

        incrementNextHandIndex();

        return sample;
    }

    private void incrementNextHandIndex()
    {
        nextHandIndex = (nextHandIndex + 1) % hands.size();
    }


    //------------------------------------------------------------------------
    @Override
    public GameState initialState()
    {
        return KuhnGameState.initialSate();
    }
}
