package ao.bucket.abstraction.bucketize.build;

import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.access.tree.BucketTreeImpl;
import ao.bucket.abstraction.bucketize.Bucketizer;
import ao.bucket.abstraction.bucketize.error.HandStrengthMeasure;
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

        if (! tree.isFlushed())
        {
            bucketizeHolesDown(
                    tree.holes(),
                    new char[]{(char)
                          numHoleBuckets,
                          numFlopBuckets,
                          numTurnBuckets,
                          numRiverBuckets},
                    tree.maxBuckets());
            tree.flush();
        }

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
        double errors[][]  = new double[ branches.size() ]
                                       [ nTrials         ];

        HandStrengthMeasure errorMeasure = new HandStrengthMeasure();
        for (int branchIndex = 0;
                 branchIndex < branches.size();
                 branchIndex++)
        {
            BucketTree.Branch turn = branches.get(branchIndex);

            for (byte nBucketTrial = 1;
                      nBucketTrial <= nTrials;
                      nBucketTrial++) {
                BUCKETIZER.bucketize(turn, nBucketTrial);
                errors[branchIndex][nBucketTrial - 1] =
                        errorMeasure.error(turn, nBucketTrial);
            }
        }

        return optimize(errors, nBuckets);
    }


    //--------------------------------------------------------------------
    // Uses pure integer proramming to optimize the best combination
    //  of sub-bucket counts.  I.e. minimizing the sum of errors.
    //
    // the contents of the output are in the form
    //   1 .. n (i.e. one based)
    private byte[] optimize(
            double errors[][],
            char   numBuckets)
    {
        
        return null;
    }
}
