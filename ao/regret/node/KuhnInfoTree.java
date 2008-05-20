package ao.regret.node;

import ao.bucket.Bucket;
import ao.regret.Equalibrium;
import ao.regret.JointBucketSequence;
import ao.simple.rules.KuhnBucket;
import ao.simple.rules.KuhnRules;
import ao.simple.rules.KuhnSequencer;

/**
 *
 */
public class KuhnInfoTree
{
    //--------------------------------------------------------------------
    public InfoNode initialize(
            Bucket<KuhnBucket> rootSequences,
            boolean            isDealer)
    {
        return new BucketNode(
                        rootSequences.nextBuckets(),
                        new KuhnRules(),
                        isDealer);
    }


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        KuhnInfoTree  tree      = new KuhnInfoTree();
        KuhnSequencer sequencer = new KuhnSequencer();

        Equalibrium equalibrium = new Equalibrium();

        InfoNode firstRoot = tree.initialize(sequencer.root(), false);
        InfoNode lastRoot  = tree.initialize(sequencer.root(), true);

        JointBucketSequence jbs = new JointBucketSequence();
        equalibrium.approximate(
                firstRoot, lastRoot,
                jbs,
                1.0, 1.0);

        System.out.println("first: " + firstRoot);
        System.out.println("last: "  + lastRoot);
    }
}
