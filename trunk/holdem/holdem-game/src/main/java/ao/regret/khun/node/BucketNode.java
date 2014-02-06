package ao.regret.khun.node;

import ao.regret.InfoNode;
import ao.regret.khun.JointBucketSequence;
import ao.simple.kuhn.rules.KuhnBucket;
import ao.simple.kuhn.rules.KuhnRules;
import ao.util.text.Txt;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 */
public class BucketNode implements InfoNode
{
    //--------------------------------------------------------------------
    private Map<KuhnBucket, PlayerNode> kids;
    private boolean                     firstToAct;


    //--------------------------------------------------------------------
    public BucketNode(
            KuhnBucket[] buckets,
            KuhnRules    rules,
            boolean      isDealer)
    {
        kids = new LinkedHashMap<KuhnBucket, PlayerNode>();

        for (KuhnBucket bucket : buckets)
        {
            PlayerNode kid =
                    (isDealer != rules.state().firstIsNextToAct())
                     ? new ProponentNode(rules, bucket)
                     : new OpponentNode(rules, bucket);

            kids.put(bucket, kid);
        }

        firstToAct = !isDealer;
    }


    //--------------------------------------------------------------------
    public PlayerNode accordingTo(JointBucketSequence jbs)
    {
        KuhnBucket b = jbs.forPlayer(firstToAct);
        return kids.get( b );
    }
    public PlayerNode accordingTo(KuhnBucket bucket)
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
        for (Map.Entry<KuhnBucket, PlayerNode> kid : kids.entrySet())
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
