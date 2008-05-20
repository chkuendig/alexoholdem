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
    public BucketNode initialize(
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

        for (int i = 0; i < 1000000; i++)
        {
            equalibrium.approximate(
                    firstRoot, lastRoot,
                    new JointBucketSequence(),
                    1.0, 1.0);
        }

        System.out.println("first:\n" + firstRoot);
        System.out.println("last:\n"  + lastRoot);
    }
}
