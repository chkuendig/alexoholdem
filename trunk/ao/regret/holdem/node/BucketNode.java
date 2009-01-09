package ao.regret.holdem.node;

import ao.holdem.engine.state.State;
import ao.regret.InfoNode;
import ao.regret.holdem.HoldemBucket;
import ao.regret.holdem.JointBucketSequence;
import ao.util.text.Txt;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Date: Jan 8, 2009
 * Time: 3:26:46 PM
 */
public class BucketNode implements InfoNode
{
    //--------------------------------------------------------------------
    private Map<HoldemBucket, PlayerNode> kids;
    private boolean                       firstToAct;
    private State                         state;


    //--------------------------------------------------------------------
    public BucketNode(
            Collection<HoldemBucket> buckets,
            State                    atState,
            boolean                  forFirstToAct)
    {
        kids       = new LinkedHashMap<HoldemBucket, PlayerNode>();
        state      = atState;
        firstToAct = forFirstToAct;

        for (HoldemBucket bucket : buckets)
        {
            PlayerNode kid =
                    (forFirstToAct ==
                        state.firstToActVoluntarelyIsNext())
                     ? new ProponentNode(state, bucket, forFirstToAct)
                     : new  OpponentNode(state, bucket, forFirstToAct);

            kids.put(bucket, kid);
        }
    }


    //--------------------------------------------------------------------
    public boolean forFirstToAct()
    {
        return firstToAct;
    }

    public PlayerNode accordingTo(JointBucketSequence jbs)
    {
        return accordingTo(
                jbs.bucket(firstToAct, state.round()));
    }
    public PlayerNode accordingTo(HoldemBucket bucket)
    {
        return kids.get( bucket );
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return toString(0);
    }

    public String toString(int depth)
    {
        StringBuilder str = new StringBuilder();
        for (Map.Entry<HoldemBucket, PlayerNode> kid : kids.entrySet())
        {
            str.append( Txt.nTimes("\t", depth) )
               .append( kid.getKey() )
               .append( "\n" )
               .append( kid.getValue().toString(depth + 1) )
               .append( "\n" );
        }
        return str.toString();
    }
}
