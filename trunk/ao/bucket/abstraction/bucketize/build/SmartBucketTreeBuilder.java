package ao.bucket.abstraction.bucketize.build;

import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.access.tree.BucketTreeImpl;
import ao.bucket.abstraction.bucketize.Bucketizer;
import ao.bucket.abstraction.bucketize.error.HandStrengthMeasure;
import ao.bucket.index.detail.CanonDetail;
import ao.bucket.index.detail.DetailLookup;
import ao.holdem.model.Round;

import java.io.File;
import java.util.List;

/**
 * User: Alex
 * Date: 21-May-2009
 * Time: 6:08:15 PM
 */
public class SmartBucketTreeBuilder implements BucketTreeBuilder
{
    //--------------------------------------------------------------------
    private final Bucketizer BUCKETIZER;


    //--------------------------------------------------------------------
    public SmartBucketTreeBuilder(Bucketizer bucketizer)
    {
        BUCKETIZER = bucketizer;
    }


    //--------------------------------------------------------------------
    public BucketTree bucketize(
            File dir,
            byte numHoleBuckets,
            char numFlopBuckets,
            char numTurnBuckets,
            char numRiverBuckets)
    {
        BucketTree tree = new BucketTreeImpl( dir );
        if (tree.isFlushed()) return tree;

        bucketizeHolesDown(
                tree.holes(),
                new char[]{(char)
                      numHoleBuckets,
                      numFlopBuckets,
                      numTurnBuckets,
                      numRiverBuckets},
                tree.maxBuckets());
        tree.flush();
        return tree;
    }


    //--------------------------------------------------------------------
    private void bucketizeHolesDown(
            BucketTree.Branch root,
            char              numBuckets[],
            byte              maxBuckets[])
    {
        BUCKETIZER.bucketize(
                root, (byte) numBuckets[Round.PREFLOP.ordinal()]);

        bucketize(root.subBranches(),
                  Round.FLOP,
                  numBuckets,
                  maxBuckets);
    }


    //--------------------------------------------------------------------
    private void bucketize(
            List<BucketTree.Branch> prevBuckets,
            Round                   round,
            char                    numBuckets[],
            byte                    maxBuckets[])
    {
        byte subBucketCounts[] =
                allocateBuckets(prevBuckets,
                                numBuckets[round.ordinal()],
                                maxBuckets[round.ordinal()]);

        for (int prevBucketIndex = 0;
                 prevBucketIndex < prevBuckets.size();
                 prevBucketIndex++)
        {
            BucketTree.Branch prevBucket =
                    prevBuckets.get(prevBucketIndex);

            BUCKETIZER.bucketize(
                    prevBuckets.get(prevBucketIndex),
                    subBucketCounts[prevBucketIndex]);

            if (round == Round.RIVER) return;
            bucketize(prevBucket.subBranches(),
                      round.next(),
                      numBuckets,
                      maxBuckets);
        }
    }


    //--------------------------------------------------------------------
    private byte[] allocateBuckets(
            List<BucketTree.Branch> branches,
            char                    nBuckets,
            byte                    nTrials)
    {
        double errors[][]   = new double[ branches.size() ]
                                        [ nTrials         ];
        int   parentPaths[] = parethReachPaths(branches);

        HandStrengthMeasure errorMeasure = new HandStrengthMeasure();
        for (int branchIndex = 0;
                 branchIndex < branches.size();
                 branchIndex++) {
            BucketTree.Branch branch = branches.get(branchIndex);

            for (byte nBucketTrial = 0;
                      nBucketTrial < nTrials;
                      nBucketTrial++) {
                BUCKETIZER.bucketize(branch, (byte)(nBucketTrial + 1));
                errors[branchIndex][nBucketTrial] =
                        errorMeasure.error(
                                branch, (byte)(nBucketTrial + 1));
            }
        }

        return Optimizer.optimize(parentPaths, errors, nBuckets);
    }

    private int[] parethReachPaths(List<BucketTree.Branch> branches)
    {
        int parentPaths[] = new int[ branches.size() ];

        for (int i = 0, branchesSize = branches.size(); i < branchesSize; i++)
        {
            BucketTree.Branch branch = branches.get(i);

            for (CanonDetail detail : DetailLookup.lookupPreRiver(
                                        branch.round().previous(),
                                        branch.parentCanons())) {
                parentPaths[ i ] += detail.represents();
            }
        }

        return parentPaths;
    }


//    //--------------------------------------------------------------------
//    // Uses pure integer proramming to optimize the best combination
//    //  of sub-bucket counts.  I.e. minimizing the sum of errors.
//    //
//    // the contents of the output are in the form
//    //   1 .. n (i.e. one based)
//    private byte[] optimize(
//            double errors[][],
//            char   numBuckets)
//    {
//
//        return null;
//    }
}
