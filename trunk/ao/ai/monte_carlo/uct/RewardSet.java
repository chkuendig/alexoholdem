package ao.ai.monte_carlo.uct;


/**
 *
 */
public class RewardSet
{
//    //--------------------------------------------------------------------
//    private Map<PlayerHandle, RewardCumulator> rewards =
//            new HashMap<PlayerHandle, RewardCumulator>();
//
//
//    //--------------------------------------------------------------------
//    public void add(PlayerHandle player, Reward reward)
//    {
//        RewardCumulator cum = rewards.get( player );
//        if (cum == null)
//        {
//            cum = new RewardCumulator( reward );
//            rewards.put( player, cum );
//        }
//        else
//        {
//            cum.add( reward );
//        }
//    }
//
//    public void add(RewardSet addend)
//    {
//        for (Map.Entry<PlayerHandle, RewardCumulator> e :
//                addend.rewards.entrySet())
//        {
//            RewardCumulator current = rewards.get( e.getKey() );
//            if (current == null)
//            {
//                rewards.put(e.getKey(),
//                            e.getValue().prototype());
//            }
//            else
//            {
//                current.add( e.getValue() );
//            }
//        }
//    }
//
//
//    //--------------------------------------------------------------------
//    public double aggregateFor(PlayerHandle player)
//    {
//        RewardCumulator cum = rewards.get(player);
//        return (cum == null)
//                ? Double.NaN
//                : cum.average();
//    }
//
//
//    //--------------------------------------------------------------------
//    public String toString()
//    {
//        return rewards.toString();
//    }
}
