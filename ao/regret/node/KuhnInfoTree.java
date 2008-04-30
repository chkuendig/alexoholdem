package ao.regret.node;

import ao.bucket.Bucket;
import ao.simple.rules.KuhnRules;

/**
 *
 */
public class KuhnInfoTree
{
    //--------------------------------------------------------------------
    public InfoNode initialize(
            Bucket  rootSequences,
            boolean isDealer)
    {
        return new BucketNode(
                        rootSequences.nextBuckets(),
                        new KuhnRules(),
                        isDealer);
    }

}
