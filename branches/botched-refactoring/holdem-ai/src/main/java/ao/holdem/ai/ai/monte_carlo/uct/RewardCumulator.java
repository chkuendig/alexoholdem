package ao.holdem.ai.ai.monte_carlo.uct;

import ao.holdem.ai.ai.monte_carlo.uct.Reward;

/**
 *
 */
public class RewardCumulator
{
    //--------------------------------------------------------------------
    private Reward cum;
    private int    count;


    //--------------------------------------------------------------------
    public RewardCumulator(Reward initialReward)
    {
        cum = initialReward;
        count++;
    }
    private RewardCumulator(Reward copyReward, int copyCount)
    {
        cum   = copyReward;
        count = copyCount;
    }


    //--------------------------------------------------------------------
    public void add(Reward reward)
    {
//        cum = (cum == null)
//               ? reward
//               : cum.plus( reward );
        cum = cum.plus( reward );
        count++;
    }

    public void add(RewardCumulator addend)
    {
        cum    = cum.plus( addend.cum );
        count += addend.count;
    }

    //--------------------------------------------------------------------
    public double average()
    {
        return cum.averagedOver( count );
    }


    //--------------------------------------------------------------------
    public RewardCumulator prototype()
    {
        return new RewardCumulator(cum, count);
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return cum + "/" + count;
    }
}
