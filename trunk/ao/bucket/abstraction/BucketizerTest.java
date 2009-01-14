package ao.bucket.abstraction;

import ao.bucket.abstraction.hole.HoleBucketLookup;
import ao.bucket.abstraction.hole.HoleBucketLookupImpl;
import ao.bucket.abstraction.hole.HoleBucketizerImpl;
import ao.bucket.abstraction.set.BucketSet;
import ao.bucket.abstraction.tree.BucketTree;
import ao.bucket.abstraction.tree.BucketTreeImpl;
import ao.bucket.index.flop.FlopLookup;
import ao.bucket.index.turn.TurnLookup;
import ao.holdem.model.Round;
import org.apache.log4j.Logger;

/**
 * Date: Oct 14, 2008
 * Time: 8:13:57 PM
 */
public class BucketizerTest
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(BucketizerTest.class);


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        testHoleLookup();
        testFlopLookup();
//        testTurnLookup();
    }


    //--------------------------------------------------------------------
    public static void testHoleLookup()
    {
        LOG.info("testing hole bucket lookup");

        char  nBuckets = 20;
        int[] counts   = new int[ nBuckets ];

        BucketTree buckets = new BucketTreeImpl("test");


//        HoleBucketLookup lookup =
//                new HoleBucketLookupImpl(new HoleBucketizerImpl());
//        BucketSet buckets = lookup.buckets( nBuckets );
//        for (int i = 0; i < Hole.CANONICAL_COUNT; i++)
//        {
//            System.out.println(
//                    i                  + "\t" +
//                    Hole.reify( i )[0] + "\t" +
//                    (int) buckets.bucketOf(i));
//            counts[ buckets.bucketOf(i) ]++;
//        }
//
//        System.out.println("summary:");
//        for (int i = 0; i < counts.length; i++)
//            System.out.println(i + "\t" + counts[i]);
    }


    //--------------------------------------------------------------------
    public static void testFlopLookup()
    {
        LOG.info("testing flop bucket lookup");

        char  numHoleBuckets = 3;
        HoleBucketLookup holeLookup =
                new HoleBucketLookupImpl(new HoleBucketizerImpl());
        BucketSet holeBuckets = holeLookup.buckets( numHoleBuckets );

        char  numFlopBuckets = 9;
        int[] counts         = new int[ numFlopBuckets ];

        CommunityBucketLookup lookup =
                new CommBucketLookupImpl(
                        Round.FLOP, new FlopBucketizerImpl());
        BucketSet buckets = lookup.buckets(holeBuckets, numFlopBuckets);
        for (int i = 0; i < FlopLookup.CANONICAL_COUNT; i++)
        {
            counts[ buckets.bucketOf(i) ]++;
        }

        for (int i = 0; i < counts.length; i++)
            System.out.println(i + "\t" + counts[i]);
    }


    //--------------------------------------------------------------------
    public static void testTurnLookup()
    {
        LOG.info("testing turn bucket lookup");

        char numHoleBuckets = 3;
        HoleBucketLookup holeLookup =
                new HoleBucketLookupImpl(new HoleBucketizerImpl());
        BucketSet holeBuckets = holeLookup.buckets( numHoleBuckets );

        char numFlopBuckets = 9;
        CommunityBucketLookup flopLookup =
                new CommBucketLookupImpl(
                        Round.FLOP, new FlopBucketizerImpl());
        BucketSet flopBuckets =
                flopLookup.buckets(holeBuckets, numFlopBuckets);

        char numTurnBuckets = 27;
        CommunityBucketLookup turnLookup =
                new CommBucketLookupImpl(
                        Round.TURN, new TurnBucketizerImpl());
        BucketSet turnBuckets =
                turnLookup.buckets(flopBuckets, numTurnBuckets);

        int[] counts = new int[ numTurnBuckets ];
        for (int i = 0; i < TurnLookup.CANON_TURN_COUNT; i++)
        {
            counts[ turnBuckets.bucketOf(i) ]++;
        }

        for (int i = 0; i < counts.length; i++)
            System.out.println(i + "\t" + counts[i]);
    }
}
