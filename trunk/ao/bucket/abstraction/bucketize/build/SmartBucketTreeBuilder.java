package ao.bucket.abstraction.bucketize.build;

import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.access.tree.BucketTreeImpl;
import ao.bucket.abstraction.bucketize.def.Bucketizer;
import ao.bucket.index.detail.CanonDetail;
import ao.bucket.index.detail.DetailLookup;
import ao.holdem.model.Round;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: Alex
 * Date: 21-May-2009
 * Time: 6:08:15 PM
 */
public class SmartBucketTreeBuilder implements BucketTreeBuilder
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(SmartBucketTreeBuilder.class);


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
        BucketTree tree = new BucketTreeImpl( dir, false );
        if (tree.isFlushed()) return tree;

        byte maxBucketBranch[] = tree.maxBucketBranch();
        bucketizeHolesDown(
                tree.holes(),
                new char[]{(char)
                      numHoleBuckets,
                      numFlopBuckets,
                      numTurnBuckets,
                      numRiverBuckets},
                new byte[]{
                    (byte) Math.min(
                             maxBucketBranch[0], numHoleBuckets * 2),
                    (byte) Math.min(
                             maxBucketBranch[1],
                             (numFlopBuckets  / numHoleBuckets) * 2),
                    maxBucketBranch[2],
                    maxBucketBranch[3]
                }
//                new byte[]{
//                    numHoleBuckets,
//                    (byte) (numFlopBuckets / numHoleBuckets),
//                    maxBucketBranch[2],
//                    maxBucketBranch[3]
//                }
        );
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
        LOG.debug("allocated: " + Arrays.toString(subBucketCounts));

        for (int prevBucketIndex = 0;
                 prevBucketIndex < prevBuckets.size();
                 prevBucketIndex++) {
            BUCKETIZER.bucketize(
                    prevBuckets.get(prevBucketIndex),
                    subBucketCounts[prevBucketIndex]);
        }

        if (round == Round.RIVER) return;
        List<BucketTree.Branch> subBranches =
                new ArrayList<BucketTree.Branch>();
        for (BucketTree.Branch prevBucket : prevBuckets) {
            subBranches.addAll( prevBucket.subBranches() );
        }
        bucketize(subBranches,
                  round.next(), numBuckets, maxBuckets);
    }


    //--------------------------------------------------------------------
    private byte[] allocateBuckets(
            List<BucketTree.Branch> branches,
            char                    nBuckets,
            byte                    nTrials)
    {
        double errors[][]    = new double[ branches.size() ]
                                         [ nTrials         ];
        int    parentPaths[] = parentReachPaths(branches);

        for (int branchIndex = 0;
                 branchIndex < branches.size();
                 branchIndex++) {
            BucketTree.Branch branch = branches.get(branchIndex);

            LOG.debug("allocating branch " + (branchIndex + 1) + " of " +
                        branches.size());
            for (byte nBucketTrial = 0;
                      nBucketTrial < nTrials;
                      nBucketTrial++) {
                errors[branchIndex][nBucketTrial] = BUCKETIZER.bucketize(
                        branch, (byte)(nBucketTrial + 1));
            }
        }

        return Optimizer.optimize(parentPaths, errors, nBuckets);
    }

    private int[] parentReachPaths(List<BucketTree.Branch> branches)
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
}
