package ao.regret.node;

import ao.bucket.Bucket;
import ao.simple.rules.KuhnRules;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class BucketNode implements InfoNode
{
    //--------------------------------------------------------------------
    private Map<Bucket, PlayerNode> kids;


    //--------------------------------------------------------------------
    public BucketNode(
            Collection<Bucket> buckets,
            KuhnRules          rules,
            boolean            isDealer)
    {
        kids = new HashMap<Bucket, PlayerNode>();

        for (Bucket bucket : buckets)
        {
            PlayerNode kid =
                    (isDealer == rules.nextToActIsDealer())
                     ? new ProponentNode(rules)
                     : new OpponentNode();

            kids.put(bucket, kid);
        }
    }
}
