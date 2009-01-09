package ao.bucket.abstraction.community.flop;

import ao.bucket.abstraction.alloc.BucketAllocator;
import ao.bucket.abstraction.alloc.SubBucketAllocator;
import ao.bucket.abstraction.community.CommunityBucketizer;
import ao.bucket.abstraction.enumeration.CanonTraverser;
import ao.bucket.abstraction.set.BucketSet;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.flop.FlopLookup;
import ao.holdem.model.Round;
import ao.odds.agglom.impl.GeneralHistFinder;
import ao.odds.eval.eval5.Eval5;
import ao.util.data.IntList;
import ao.util.misc.Traverser;
import ao.util.persist.PersistentShorts;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * User: AOstrovsky
 * Date: Dec 14, 2008
 * Time: 4:25:59 PM
 */
public class FlopBucketizerImpl
        implements CommunityBucketizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(FlopBucketizerImpl.class);

    private static final String  MEAN_FILE_DIR =
            "lookup/bucket/" + Round.FLOP.toString() + "/";
    private static final String  MEAN_FILE =
            MEAN_FILE_DIR + "means.bin";
    private static final short[] MEANS =
            computeOrRetrieveCanonFlopMeans();


    //--------------------------------------------------------------------
    private static short[] computeOrRetrieveCanonFlopMeans()
    {
        LOG.debug("attempting to retrieve canon flop means");
        if (new File(MEAN_FILE_DIR).mkdirs())
            LOG.debug("created " + MEAN_FILE_DIR);

        short[] means = PersistentShorts.readBinary(MEAN_FILE);
        if (means == null)
        {
            means = computeCanonFlopMeans();
            PersistentShorts.writeBinary(means, MEAN_FILE);
        }
        
        LOG.debug("done retrieving/computing canon flop means");
        return means;
    }
    private static short[] computeCanonFlopMeans()
    {
        LOG.debug("computing canon flop means");

        final short[] means = new short[ FlopLookup.CANON_FLOP_COUNT ];
        final int[]   count = new int[1];
        new CanonTraverser().traverseFlops(new Traverser<Flop>() {
            public void traverse(Flop flop) {
                means[ flop.canonIndex() ] =
                        (short) Math.round(
                          new GeneralHistFinder().compute(
                                  flop.hole(), flop.community()).mean());
                if (count[0]++ % 10000 == 0) System.out.print(".");
            }
        });
        System.out.println();

        return means;
    }


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
                      FlopLookup.CANON_FLOP_COUNT, numBuckets);

        char   bucketOffset = 0;
        byte[] bucketAlloc  = new SubBucketAllocator().allocate(
                onTopOf.bucketCount(), numBuckets);
        for (char holeBucket = 0;
                  holeBucket < onTopOf.bucketCount();
                  holeBucket++)
        {
            bucketize(
                    buckets,
                    byMean(onTopOf.canonsOf( holeBucket )),
                    (char) bucketAlloc[ holeBucket ],
                    bucketOffset);

            bucketOffset += bucketAlloc[ holeBucket ];
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
    private IntList[] byMean(long[] forHoles)
    {
        LOG.debug("sorting flops by mean for " +
                    forHoles.length + " canon holes");

        final IntList[] byMean = new IntList[ Eval5.VALUE_COUNT ];
        new CanonTraverser().traverseFlops(
                forHoles, new Traverser<Flop>() {
            public void traverse(Flop flop) {
                short meanStrength = MEANS[ flop.canonIndex() ];
                byMean[ meanStrength ] =
                    IntList.addTo(byMean[ meanStrength ],
                                  flop.canonIndex());
            }
        });

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
