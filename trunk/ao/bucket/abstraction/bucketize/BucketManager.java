package ao.bucket.abstraction.bucketize;

import ao.bucket.abstraction.tree.BucketTree;
import ao.bucket.abstraction.tree.BucketTree.Branch;
import ao.bucket.abstraction.tree.BucketTreeImpl;

import java.util.List;

/**
 * Date: Jan 8, 2009
 * Time: 11:01:12 AM
 */
public class BucketManager
{
    //--------------------------------------------------------------------
    private final Bucketizer HOLE_BUCKETIZER;
    private final Bucketizer FLOP_BUCKETIZER;
    private final Bucketizer TURN_BUCKETIZER;
    private final Bucketizer RIVER_BUCKETIZER;


    //--------------------------------------------------------------------
    public BucketManager(Bucketizer bucketizer)
    {
        this(bucketizer, bucketizer, bucketizer, bucketizer);
    }
    public BucketManager(Bucketizer holeBucketizer,
                         Bucketizer flopBucketizer,
                         Bucketizer turnBucketizer,
                         Bucketizer riverBucketizer)
    {
        HOLE_BUCKETIZER  = holeBucketizer;
        FLOP_BUCKETIZER  = flopBucketizer;
        TURN_BUCKETIZER  = turnBucketizer;
        RIVER_BUCKETIZER = riverBucketizer;
    }


    //--------------------------------------------------------------------
    public BucketTree bucketize(
            byte numHoleBuckets,
            char numFlopBuckets,
            char numTurnBuckets,
            char numRiverBuckets)
    {
        BucketTree tree = new BucketTreeImpl(
                treeId(numHoleBuckets, numFlopBuckets,
                       numTurnBuckets, numRiverBuckets));

        bucketizeHolesDown(
                tree.root(),
                numHoleBuckets,
                numFlopBuckets,
                numTurnBuckets,
                numRiverBuckets);

        return tree;
    }

    private String treeId(
            byte numHoleBuckets,
            char numFlopBuckets,
            char numTurnBuckets,
            char numRiverBuckets)
    {
        StringBuilder str = new StringBuilder();

        str.append( HOLE_BUCKETIZER.id() )
           .append( '.' )
           .append( (int) numHoleBuckets )
           .append( '|' )
           .append( FLOP_BUCKETIZER.id() )
           .append( '.' )
           .append( (int) numFlopBuckets )
           .append( '|' )
           .append( TURN_BUCKETIZER.id() )
           .append( '.' )
           .append( (int) numTurnBuckets )
           .append( '|' )
           .append( RIVER_BUCKETIZER.id() )
           .append( '.' )
           .append( (int) numRiverBuckets );

        return str.toString();
    }



    //--------------------------------------------------------------------
    private void bucketizeHolesDown(
            Branch root,
            byte   numHoleBuckets,
            char   numFlopBuckets,
            char   numTurnBuckets,
            char   numRiverBuckets)
    {
        HOLE_BUCKETIZER.bucketize(root, numHoleBuckets);

//        bucketizeFlopsDown(
//                root.subBranches(),
//                new SubBucketAllocator().allocate(
//                        (char) numHoleBuckets, numFlopBuckets),
//                new SubBucketAllocator().allocate(
//                        numFlopBuckets, numTurnBuckets),
//                new SubBucketAllocator().allocate(
//                        numTurnBuckets, numRiverBuckets));
    }


    //--------------------------------------------------------------------
    private void bucketizeFlopsDown(
            List<Branch> holes,
            byte[]       flopBucketCounts,
            byte[]       turnBucketCounts,
            byte[]       riverBucketCounts)
    {
        char turnBuckets     = 0;
        char holeBucketIndex = 0;
        for (Branch hole : holes)
        {
            FLOP_BUCKETIZER.bucketize(
                    hole, flopBucketCounts[ holeBucketIndex++ ]);

            turnBuckets += bucketizeTurnsDown(
                                hole.subBranches(),
                                turnBuckets,
                                turnBucketCounts,
                                riverBucketCounts);
        }
    }


    //--------------------------------------------------------------------
    private char bucketizeTurnsDown(
            List<Branch> flops,
            char         turnBucketOffset,
            byte[]       turnBucketCounts,
            byte[]       riverBucketCounts)
    {
        char riverBuckets    = 0;
        char flopBucketIndex = 0;
        for (Branch flop : flops)
        {
            TURN_BUCKETIZER.bucketize(
                    flop, turnBucketCounts[
                            turnBucketOffset + (flopBucketIndex++) ]);

            riverBuckets += bucketizeRivers(
                                flop.subBranches(),
                                riverBuckets,
                                riverBucketCounts);
        }
        return flopBucketIndex;
    }


    //--------------------------------------------------------------------
    private char bucketizeRivers(
            List<Branch> turns,
            char         riverBucketOffset,
            byte[]       riverBucketCounts)
    {
        char turnBucketIndex = 0;
        for (Branch turn : turns)
        {
            RIVER_BUCKETIZER.bucketize(
                    turn, riverBucketCounts[
                            riverBucketOffset + (turnBucketIndex++) ]);
        }
        return turnBucketIndex;
    }
}
