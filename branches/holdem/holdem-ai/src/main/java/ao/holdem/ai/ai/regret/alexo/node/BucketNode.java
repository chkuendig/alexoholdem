package ao.holdem.ai.ai.regret.alexo.node;

import ao.holdem.ai.ai.regret.InfoNode;
import ao.holdem.ai.ai.regret.alexo.AlexoBucket;
import ao.holdem.ai.ai.regret.alexo.JointBucketSequence;
import ao.holdem.ai.ai.simple.alexo.state.AlexoState;
import ao.util.text.Txt;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class BucketNode implements InfoNode
{
    //--------------------------------------------------------------------
    private Map<AlexoBucket, PlayerNode> kids;
    private boolean                      firstToAct;
    private AlexoState                   state;


    //--------------------------------------------------------------------
    public BucketNode(
            AlexoBucket[] buckets,
            AlexoState    atState,
            boolean       forFirstToAct)
    {
        kids       = new LinkedHashMap<AlexoBucket, PlayerNode>();
        state      = atState;
        firstToAct = forFirstToAct;

        for (AlexoBucket bucket : buckets)
        {
            PlayerNode kid =
                    (forFirstToAct == state.firstToActIsNext())
                     ? new ProponentNode(state, bucket, forFirstToAct)
                     : new  OpponentNode(state, bucket, forFirstToAct);

            kids.put(bucket, kid);
        }
    }


//    //--------------------------------------------------------------------
//    public BucketPair pair(BucketNode withLast)
//    {
//        return new BucketPair(state, this);
//    }


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
    public PlayerNode accordingTo(AlexoBucket bucket)
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
        for (Map.Entry<AlexoBucket, PlayerNode> kid : kids.entrySet())
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
