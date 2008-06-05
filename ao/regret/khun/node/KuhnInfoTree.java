package ao.regret.khun.node;

import ao.bucket.Bucket;
import ao.regret.InfoNode;
import ao.regret.khun.Equalibrium;
import ao.regret.khun.JointBucketSequence;
import ao.simple.kuhn.rules.KuhnBucket;
import ao.simple.kuhn.rules.KuhnRules;
import ao.simple.kuhn.rules.KuhnSequencer;

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
