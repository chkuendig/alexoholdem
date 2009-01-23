package ao.bucket.abstraction;

import ao.bucket.abstraction.access.BucketMap;
import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.bucketize.BucketManager;
import ao.bucket.abstraction.bucketize.BucketizerImpl;
import ao.bucket.index.detail.DetailLookup;
import ao.bucket.index.detail.flop.CanonFlopDetail;
import ao.bucket.index.detail.preflop.CanonHoleDetail;
import ao.bucket.index.detail.turn.TurnDetailFlyweight.CanonTurnDetail;
import ao.bucket.index.hole.HoleLookup;

import java.util.Arrays;

/**
 * Date: Oct 14, 2008
 * Time: 8:13:57 PM
 */
public class BucketizerTest
{
//    //--------------------------------------------------------------------
//    private static final Logger LOG =
//            Logger.getLogger(BucketizerTest.class);


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        test((byte) (5),
             (char) (5 * 10),
             (char) (5 * 10 * 3),
             (char) (5 * 10 * 3 * 3));
    }


    //--------------------------------------------------------------------
    public static void test(
            byte nHoleBuckets,
            char nFlopBuckets,
            char nTurnBuckets,
            char nRiverBuckets)
    {
        BucketManager manager =
                new BucketManager( new BucketizerImpl() );

        BucketTree buckets = manager.bucketize(
                                nHoleBuckets,
                                nFlopBuckets,
                                nTurnBuckets,
                                nRiverBuckets);
        BucketMap  map    = new BucketMap( buckets );

        int[] holeCounts = new int[ nHoleBuckets ];
        int[] flopCounts = new int[ nFlopBuckets ];
        int[] turnCounts = new int[ nTurnBuckets ];

        for (CanonHoleDetail holeDetail :
                DetailLookup.lookupHole(
                        (char) 0, (char) HoleLookup.CANONICAL_COUNT))
        {
            char holeIndex = (char) holeDetail.canonIndex();
            holeCounts[ buckets.getHole(holeIndex) ]++;

            for (CanonFlopDetail flopDetail :
                    DetailLookup.lookupFlop(
                            holeDetail.firstCanonFlop(),
                            holeDetail.canonFlopCount()))
            {
                int flopIndex = (int) flopDetail.canonIndex();
                flopCounts[ map.serialize(
                                buckets.getHole(holeIndex),
                                buckets.getFlop(flopIndex)) ]++;

                for (CanonTurnDetail turnDetail :
                        DetailLookup.lookupTurn(
                                flopDetail.firstCanonTurn(),
                                flopDetail.canonTurnCount()))
                {
                    int turnIndex = (int) turnDetail.canonIndex();
                    turnCounts[ map.serialize(
                                    buckets.getHole(holeIndex),
                                    buckets.getFlop(flopIndex),
                                    buckets.getTurn(turnIndex)) ]++;
                }
            }
        }

        System.out.println(Arrays.toString(holeCounts));
        System.out.println(Arrays.toString(flopCounts));
        System.out.println(Arrays.toString(turnCounts));
    }
}
