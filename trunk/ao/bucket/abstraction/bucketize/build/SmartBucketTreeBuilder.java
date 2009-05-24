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
    private static final double  FLOP_FACTOR = 2;
    private static final double  TURN_FACTOR = 2;
    private static final double RIVER_FACTOR = 2;


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
                    numHoleBuckets,
                    numFlopBuckets,
                    numTurnBuckets,
                    numRiverBuckets);
            tree.flush();
        }

        return tree;
    }

    //--------------------------------------------------------------------
    private void bucketizeHolesDown(
            final BucketTree.Branch root,
            final byte              numHoleBuckets,
            final char              numFlopBuckets,
            final char              numTurnBuckets,
            final char              numRiverBuckets)
    {
        BUCKETIZER.bucketize(root, numHoleBuckets);

        bucketize(root.subBranches(),
                  Round.FLOP,
                  new char[]{(char)
                          numHoleBuckets,
                          numFlopBuckets,
                          numTurnBuckets,
                          numRiverBuckets});
    }


    //--------------------------------------------------------------------
    private void bucketize(
            final List<BucketTree.Branch> prevBuckets,
            final Round                   round,
            final char                    numBuckets[])
    {
        byte subBucketCounts[] =
                allocateBuckets(prevBuckets,
                                numBuckets[round.ordinal()],
                                (byte) 16);

        for (int prevBucketIndex = 0;
                 prevBucketIndex < prevBuckets.size();
                 prevBucketIndex++)
        {
            BucketTree.Branch prevBucket =
                    prevBuckets.get(prevBucketIndex);

            BUCKETIZER.bucketize(
                    prevBuckets.get(prevBucketIndex),
                    subBucketCounts[prevBucketIndex]);

            if (round == Round.TURN) return;
            bucketize(
                    prevBucket.subBranches(),
                    round.next(),
                    numBuckets);
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
