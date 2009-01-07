package ao.bucket.abstraction.hole;

import ao.bucket.abstraction.alloc.BucketAllocator;
import ao.bucket.abstraction.set.BucketSet;
import ao.bucket.index.CanonTraverser;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.odds.agglom.impl.PreciseHeadsUpOdds;
import ao.util.misc.Traverser;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Comparator;


/**
 *
 */
public class HoleBucketizerImpl
        implements HoleBucketizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(HoleBucketizerImpl.class);


    //--------------------------------------------------------------------
    private static final Double strengths[] =
            new Double[ Hole.CANONICAL_COUNT ];
    private static final Short  inOrder[]   =
            new Short [ Hole.CANONICAL_COUNT ];

    private static void initCanonHoles()
    {
        LOG.info("computing hole strengths");

        new CanonTraverser().traverseHoles(new Traverser<Hole>() {
            public void traverse(Hole hole) {

                strengths[ hole.canonIndex() ] =
                        new PreciseHeadsUpOdds().compute(
                                hole, Community.PREFLOP
                        ).strengthVsRandom();
            }
        });

        for (short i = 0; i < inOrder.length; i++) inOrder[ i ] = i;
        Arrays.sort(inOrder, new Comparator<Short>() {
            public int compare(Short a, Short b) {
                return strengths[ a ].compareTo(
                            strengths[ b ]);
            }
        });
    }


    //--------------------------------------------------------------------
    public <T extends BucketSet> T
            bucketize(char nBuckets, BucketSet.Builder<T> into)
    {
        assert nBuckets > 0;
        if (strengths[0] == null) initCanonHoles();

        T               buckets = into.newInstance(
                          Hole.CANONICAL_COUNT, nBuckets);
        BucketAllocator alloc   = new BucketAllocator(
                          Hole.CANONICAL_COUNT, nBuckets);
        
        for (Short holeCanon : inOrder)
        {
            buckets.add(holeCanon, alloc.nextBucket(1));
        }

        return buckets;
    }


    //--------------------------------------------------------------------
    public String id()
    {
        return getClass().getSimpleName();
    }

    @Override
    public String toString()
    {
        return id();
    }
}