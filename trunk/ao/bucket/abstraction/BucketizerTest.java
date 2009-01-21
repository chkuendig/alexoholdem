package ao.bucket.abstraction;

import ao.bucket.abstraction.access.BucketMap;
import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.bucketize.BucketManager;
import ao.bucket.abstraction.bucketize.BucketizerImpl;
import ao.bucket.index.detail.DetailLookup;
import ao.bucket.index.detail.flop.CanonFlopDetail;
import ao.bucket.index.detail.preflop.CanonHoleDetail;
import ao.bucket.index.hole.HoleLookup;
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
//        testHoleLookup();
        testFlopLookup();
//        testTurnLookup();
    }


    //--------------------------------------------------------------------
    public static void testHoleLookup()
    {
        LOG.info("testing hole bucket lookup");

        byte  nBuckets = 20;
        int[] counts   = new int[ nBuckets ];

        BucketManager manager =
                new BucketManager( new BucketizerImpl() );

        BucketTree buckets = manager.bucketize(
                nBuckets, (char) 4, (char) 8, (char) 16);

        for (char i = 0; i < HoleLookup.CANONICAL_COUNT; i++)
        {
            System.out.println(
                    ((int) i)                              + "\t" +
                    DetailLookup.lookupHole( i ).example() + "\t" +
                    (int) buckets.getHole( i ));
            counts[ buckets.getHole(i) ]++;
        }

        System.out.println("summary:");
        for (int i = 0; i < counts.length; i++)
            System.out.println(i + "\t" + counts[i]);
    }


    //--------------------------------------------------------------------
    public static void testFlopLookup()
    {
        LOG.info("testing flop bucket lookup");

        byte numHoleBuckets = 13;
        char numFlopBuckets = 1134;

        BucketManager manager =
                new BucketManager( new BucketizerImpl() );

        BucketTree buckets = manager.bucketize(
                numHoleBuckets,
                numFlopBuckets,
                (char) 8, (char) 16);

        BucketMap map     = new BucketMap( buckets );
        int[]     counts  = new int[ numFlopBuckets ];

        for (CanonHoleDetail holeDetail :
                DetailLookup.lookupHole(
                        (char) 0, (char) HoleLookup.CANONICAL_COUNT))
        {
            for (CanonFlopDetail flopDetail :
                    DetailLookup.lookupFlop(
                            holeDetail.firstCanonFlop(),
                            holeDetail.canonFlopCount()))
            {
                counts[ map.serialize(
                            buckets.getHole(
                                    (char) holeDetail.canonIndex()),
                            buckets.getFlop(
                                    (int)  flopDetail.canonIndex()))
                      ]++;
            }
        }

        for (int i = 0; i < counts.length; i++)
            System.out.println(i + "\t" + counts[i]);
    }


//    //--------------------------------------------------------------------
//    public static void testTurnLookup()
//    {
//        LOG.info("testing turn bucket lookup");
//
//        char numHoleBuckets = 3;
//        HoleBucketLookup holeLookup =
//                new HoleBucketLookupImpl(new HoleBucketizerImpl());
//        BucketSet holeBuckets = holeLookup.buckets( numHoleBuckets );
//
//        char numFlopBuckets = 9;
//        CommunityBucketLookup flopLookup =
//                new CommBucketLookupImpl(
//                        Round.FLOP, new FlopBucketizerImpl());
//        BucketSet flopBuckets =
//                flopLookup.buckets(holeBuckets, numFlopBuckets);
//
//        char numTurnBuckets = 27;
//        CommunityBucketLookup turnLookup =
//                new CommBucketLookupImpl(
//                        Round.TURN, new TurnBucketizerImpl());
//        BucketSet turnBuckets =
//                turnLookup.buckets(flopBuckets, numTurnBuckets);
//
//        int[] counts = new int[ numTurnBuckets ];
//        for (int i = 0; i < TurnLookup.CANONICAL_COUNT; i++)
//        {
//            counts[ turnBuckets.bucketOf(i) ]++;
//        }
//
//        for (int i = 0; i < counts.length; i++)
//            System.out.println(i + "\t" + counts[i]);
//    }
}
