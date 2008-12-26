package ao.bucket.abstraction.flop;

import ao.bucket.abstraction.set.BucketSet;
import ao.bucket.index.CanonTraverser;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.flop.FlopLookup;
import ao.holdem.model.card.Hole;
import ao.odds.agglom.impl.GeneralHistFinder;
import ao.odds.eval.eval5.Eval5;
import ao.util.data.IntList;
import ao.util.misc.Traverser;
import org.apache.log4j.Logger;

/**
 * User: AOstrovsky
 * Date: Dec 14, 2008
 * Time: 4:25:59 PM
 */
public class FlopBucketizerImpl<T extends BucketSet>
        implements FlopBucketizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(FlopBucketizerImpl.class);


    //--------------------------------------------------------------------
    private final BucketSet.Builder<T> BUCKET_SET_BUILDER;


    //--------------------------------------------------------------------
    public FlopBucketizerImpl(BucketSet.Builder<T> builder)
    {
        BUCKET_SET_BUILDER = builder;
    }


    //--------------------------------------------------------------------
    public T bucketize(BucketSet onTopOf, char numBuckets)
    {
        assert numBuckets >= 1;
        LOG.debug("bucketizing flop into " + numBuckets + " buckets");

        T buckets = BUCKET_SET_BUILDER.newInstance(
                        FlopLookup.CANON_FLOP_COUNT, numBuckets);

        char nextBucket = 0, lastBucket = (char)(numBuckets - 1);
        int  bucketFill = 0;
        int  perBucket  = FlopLookup.CANON_FLOP_COUNT / numBuckets;
        for (IntList meanStrata : byMean())
        {
            if (meanStrata == null) continue;

            for (int i = meanStrata.size() - 1; i >= 0; i--)
            {
                buckets.add( meanStrata.get(i), nextBucket );
            }

            if (bucketFill >= perBucket &&
                    nextBucket != lastBucket)
            {
                bucketFill = 0;
                nextBucket++;
            }
        }

        return buckets;
    }


    //--------------------------------------------------------------------
    private IntList[] byMean()
    {
        final IntList[] byMean =
                new IntList[ Eval5.VALUE_COUNT ];

        new CanonTraverser().traverseFlops(new Traverser<Flop>() {
            public void traverse(Flop flop) {
                Hole hole  = flop.hole();
                int  mean  = (int) Math.round(
                        new GeneralHistFinder().compute(
                                hole, flop.toCommunity()).mean());
                int  index = flop.canonIndex();

                byMean[ mean ] = IntList.addTo(byMean[ mean ], index);
            }
        });

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
