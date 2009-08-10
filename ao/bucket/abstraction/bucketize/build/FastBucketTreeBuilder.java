package ao.bucket.abstraction.bucketize.build;

import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.access.tree.BucketTree.Branch;
import ao.bucket.abstraction.access.tree.BucketTreeImpl;
import ao.bucket.abstraction.alloc.SubBucketAllocator;
import ao.bucket.abstraction.bucketize.def.Bucketizer;

import java.io.File;

/**
 * Date: Jan 8, 2009
 * Time: 11:01:12 AM
 */
public class FastBucketTreeBuilder implements BucketTreeBuilder
{
    //--------------------------------------------------------------------
    private final Bucketizer BUCKETIZER;


    //--------------------------------------------------------------------
    public FastBucketTreeBuilder(Bucketizer bucketizer)
    {
        BUCKETIZER = bucketizer;
    }


    //--------------------------------------------------------------------
    public BucketTree bucketize(
            File dir,
            int  numHoleBuckets,
            char numFlopBuckets,
            char numTurnBuckets,
            char numRiverBuckets)
    {
        BucketTree tree = new BucketTreeImpl(dir, false);

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
            final Branch holes,
            final int    numHoleBuckets,
            final char   numFlopBuckets,
            final char   numTurnBuckets,
            final char   numRiverBuckets)
    {
        BUCKETIZER.bucketize(holes, numHoleBuckets);

//        HandEnum.holes(new PermisiveFilter<CanonHole>(),
//                new Traverser<CanonHole>() {
//                    public void traverse(CanonHole canonHole) {
//                        System.out.println(
//                                canonHole    + "\t" +
//                                HoleDetails.compact(
//                                        canonHole.canonIndex()
//                                ).strength() + "\t" +
//                                holes.get(canonHole.canonIndex())
//                        );
//                    }
//                });


        bucketizeFlopsDown(
                holes.subBranches(),
                new SubBucketAllocator().allocate(
                        (char) numHoleBuckets, numFlopBuckets),
                new SubBucketAllocator().allocate(
                        numFlopBuckets, numTurnBuckets),
                new SubBucketAllocator().allocate(
                        numTurnBuckets, numRiverBuckets));
    }


    //--------------------------------------------------------------------
    private void bucketizeFlopsDown(
            Iterable<Branch> flops,
            byte[]           flopBucketCounts,
            byte[]           turnBucketCounts,
            byte[]           riverBucketCounts)
    {
        char turnBuckets     = 0;
        char holeBucketIndex = 0;
        for (Branch flop : flops)
        {
            BUCKETIZER.bucketize(
                    flop, flopBucketCounts[ holeBucketIndex++ ]);

            turnBuckets += bucketizeTurnsDown(
                                flop.subBranches(),
                                turnBuckets,
                                turnBucketCounts,
                                riverBucketCounts);
        }
    }


    //--------------------------------------------------------------------
    private char bucketizeTurnsDown(
            Iterable<Branch> turns,
            char             turnBucketOffset,
            byte[]           turnBucketCounts,
            byte[]           riverBucketCounts)
    {
        char riverBuckets    = 0;
        char flopBucketIndex = 0;
        for (Branch turn : turns)
        {
            BUCKETIZER.bucketize(
                    turn, turnBucketCounts[
                            turnBucketOffset + (flopBucketIndex++) ]);

            riverBuckets += bucketizeRivers(
                                turn.subBranches(),
                                riverBuckets,
                                riverBucketCounts);
        }
        return flopBucketIndex;
    }


    //--------------------------------------------------------------------
    private char bucketizeRivers(
            Iterable<Branch> rivers,
            char             riverBucketOffset,
            byte[]           riverBucketCounts)
    {
        char turnBucketIndex = 0;
        for (Branch river : rivers)
        {
            BUCKETIZER.bucketize(
                    river, riverBucketCounts[
                            riverBucketOffset + (turnBucketIndex++) ]);
        }
        return turnBucketIndex;
    }
}
