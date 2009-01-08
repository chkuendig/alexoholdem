package ao.bucket.abstraction.community.turn;

import ao.bucket.abstraction.alloc.BucketAllocator;
import ao.bucket.abstraction.alloc.SubBucketAllocator;
import ao.bucket.abstraction.community.CommunityBucketizer;
import ao.bucket.abstraction.set.BucketSet;
import ao.bucket.index.CanonTraverser;
import ao.bucket.index.turn.Turn;
import ao.bucket.index.turn.TurnLookup;
import ao.odds.agglom.impl.GeneralHistFinder;
import ao.odds.eval.eval5.Eval5;
import ao.util.data.IntList;
import ao.util.misc.Traverser;
import org.apache.log4j.Logger;

/**
 * Date: Dec 26, 2008
 * Time: 4:34:23 PM
 */
public class TurnBucketizerImpl implements CommunityBucketizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(TurnBucketizerImpl.class);


    //--------------------------------------------------------------------
    public <T extends BucketSet> T
            bucketize(BucketSet            onTopOf,
                      char                 numBuckets,
                      BucketSet.Builder<T> with)
    {
        assert numBuckets >= 1;
        LOG.debug("bucketizing flop into " +
                  (int)(numBuckets) + " buckets");

        T buckets = with.newInstance(
                      TurnLookup.CANON_TURN_COUNT, numBuckets);

        char   bucketOffset = 0;
        char[] bucketAlloc  = new SubBucketAllocator().allocate(
                onTopOf.bucketCount(), numBuckets);
        for (char flopBucket = 0;
                  flopBucket < onTopOf.bucketCount();
                  flopBucket++)
        {
            bucketize(
                    buckets,
                    byMean(onTopOf.canonsOf( flopBucket )),
                    bucketAlloc[ flopBucket ],
                    bucketOffset);

            bucketOffset += bucketAlloc[ flopBucket ];
        }

        return buckets;
    }


    //--------------------------------------------------------------------
    private void
            bucketize(BucketSet into,
                      IntList[] meanStrata,
                      char      numBuckets,
                      char      bucketOffset)
    {
        BucketAllocator alloc = new BucketAllocator(
                IntList.sizeSum(meanStrata), numBuckets);

        for (IntList stratum : meanStrata)
        {
            if (stratum == null) continue;

            char nextBucket = alloc.nextBucket(stratum.size());
            for (int i = stratum.size() - 1; i >= 0; i--)
            {
                into.add(stratum.get(i),
                         (char)(bucketOffset + nextBucket));
            }
        }
    }


    //--------------------------------------------------------------------
    private IntList[] byMean(long[] forFlops)
    {
        LOG.debug("sorting turns by mean for " +
                    forFlops.length + " canon flops");

        final int[]     count  = new int[1];
        final IntList[] byMean = new IntList[ Eval5.VALUE_COUNT ];
        new CanonTraverser().traverseTurns(
                forFlops, new Traverser<Turn>() {
            public void traverse(Turn turn) {
                short meanStrength = (short) Math.round(
                        new GeneralHistFinder().compute(
                                turn.hole(), turn.community()).mean());
                byMean[ meanStrength ] =
                    IntList.addTo(byMean[ meanStrength ],
                                  turn.canonIndex());

                if (count[0]++ % 100000 == 0) System.out.print(".");
            }
        });
        System.out.println();

        LOG.debug("done sorting by mean");
        return byMean;
    }


    //--------------------------------------------------------------------
    public String id()
    {
        return getClass().getSimpleName();
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return id();
    }
}
