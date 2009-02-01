package ao.regret.holdem;

import ao.bucket.abstraction.access.BucketAgglom;
import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.bucketize.BucketManager;
import ao.bucket.abstraction.bucketize.BucketizerImpl;
import ao.holdem.engine.state.StateTree;
import ao.regret.holdem.node.BucketNode;
import ao.regret.holdem.pair.BucketPair;


/**
 * Date: Feb 1, 2009
 * Time: 2:25:50 PM
 */
public class HoldemInfoTree
{
    //--------------------------------------------------------------------
    public BucketPair root(BucketAgglom buckets)
    {
        System.out.println("computing info tree");

        return new BucketPair(
                    StateTree.headsUpRoot(), buckets.root());
    }


    //--------------------------------------------------------------------
    public static void main(String args[])
    {
        BucketAgglom buckets = buckets(6, 144, 432, 1296);


        new BucketNode(buckets.root(), StateTree.headsUpRoot(), true);

//        BucketPair   root    = new HoldemInfoTree().root(buckets);
//
////        System.out.println( root );
//        for (int i = 0; i < 1000 * 1000; i++)
//        {
//            System.out.println("!");
//            root.approximate(
//                    JointBucketSequence.randomInstance(buckets));
//
//            if ( i      %     1  == 0) System.out.print(".");
//            if ((i + 1) % (50*1) == 0) System.out.println();
//
//        }
////        System.out.println( root );
    }

    //--------------------------------------------------------------------
    public static BucketAgglom buckets(
            int nHoleBuckets,
            int nFlopBuckets,
            int nTurnBuckets,
            int nRiverBuckets)
    {
        BucketManager manager =
                new BucketManager( new BucketizerImpl() );

        BucketTree buckets  = manager.bucketize(
                                (byte) nHoleBuckets,
                                (char) nFlopBuckets,
                                (char) nTurnBuckets,
                                (char) nRiverBuckets);

        return new BucketAgglom( buckets );
    }
}
