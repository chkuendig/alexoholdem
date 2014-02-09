package ao.holdem.abs.bucket.abstraction.bucketize.linear;

import ao.holdem.abs.bucket.abstraction.access.tree.BucketTree;
import ao.holdem.abs.bucket.abstraction.alloc.BucketAllocator;
import ao.holdem.abs.bucket.abstraction.bucketize.def.ScalarBucketizer;
import ao.holdem.abs.bucket.abstraction.bucketize.error.HandStrengthMeasure;
import ao.holdem.model.Round;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.concurrent.ConcurrentMap;

/**
 * User: alex
 * Date: 5-May-2009
 * Time: 8:51:58 PM
 */
public class PercentileAbs implements ScalarBucketizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(PercentileAbs.class);


    //--------------------------------------------------------------------
    private static final ConcurrentMap<BucketTree.Branch, IndexedStrengthList> sortedDetails =
            CacheBuilder.newBuilder()
            .softValues()
            .build(new CacheLoader<BucketTree.Branch, IndexedStrengthList>() {
                @Override public IndexedStrengthList load(BucketTree.Branch branch) {
                    LOG.info("computing: " + branch);
                    return computeSortedDetails(branch);
                }
            }).asMap();

    private static IndexedStrengthList computeSortedDetails(
            BucketTree.Branch branch)
    {
        IndexedStrengthList strengths =
                IndexedStrengthList.strengths(branch);
        Collections.sort( strengths );
        return strengths;

//        CanonDetail[] details = branch.details();
//        Arrays.sort(details, new Comparator<CanonDetail>() {
//            public int compare(CanonDetail a, CanonDetail b) {
//                return Double.compare(
//                         a.strength(), b.strength());
//            }
//        });
//        return details;
    }



    //--------------------------------------------------------------------
    public double bucketize(BucketTree.Branch branch, int nBuckets)
    {
        assert nBuckets > 0;

        if (branch.round() == Round.RIVER) {
            return bucketizeRiver(branch, nBuckets);
        } else {
            return bucketizeByList(branch, nBuckets);
        }
    }

    public double bucketize(
            BucketTree.Branch   branch,
            IndexedStrengthList details,
            int                 numBuckets) {
        return bucketize(branch, numBuckets);
    }


    //--------------------------------------------------------------------
    private double bucketizeByList(BucketTree.Branch branch, int nBuckets)
    {
        LOG.debug("bucketizing " + branch.round() + " into " + nBuckets);

//        IndexedStrengthList strengthList =
//                IndexedStrengthList.strengths(branch);

//        CanonDetail[] details = branch.details();
//        Arrays.sort(details, new Comparator<CanonDetail>() {
//            public int compare(CanonDetail a, CanonDetail b) {
//                return Double.compare(
//                         a.strength(), b.strength());
//            }
//        });

        IndexedStrengthList details   = sortedDetails.get( branch );

        long cardCount = 0;
        for (int i = 0; i < details.size(); i++) {
            cardCount += details.represents( i );
        }

        BucketAllocator alloc = new BucketAllocator(
                          cardCount, (char) nBuckets);
        for (int i = 0; i < details.size(); i++)
        {
//            long   index    = detail.canonIndex();
//            int    bucket   = (int) alloc.nextBucket(1);
//            double strength = detail.strength();

//            error.add(bucket, strength);
            branch.set(details.index(i),
                       (int) alloc.nextBucket(
                               details.represents(i)));
        }

//        for (CanonDetail detail : details)
//        {
//            int    bucket   = (int) alloc.nextBucket(1);
//            double strength = detail.strength();
//
//            error.check(bucket, strength);
//        }

//        return error.error();
        return new HandStrengthMeasure().error(
                     branch, details, nBuckets);
    }


    //--------------------------------------------------------------------
    private double bucketizeRiver(
            final BucketTree.Branch branch, final int nBuckets)
    {
        assert nBuckets > 0;

//        int        nRivers       = 0;
//        CanonRange toBucketize[] =
//                new CanonRange[ branch.parentCanons().length ];
//        for (int i = 0; i < branch.parentCanons().length; i++) {
//
//            int canonTurn = branch.parentCanons()[i];
//            toBucketize[ i ] = TurnDetails.lookup(canonTurn).range();
//            nRivers += toBucketize[ i ].count();
//        }
//        Arrays.sort(toBucketize);

        IndexedStrengthList details   = sortedDetails.get( branch );

        LOG.debug("bucketizing " + branch.round() +
                  " into " + nBuckets +
                  " (p " + branch.parentCanons().length +
                  ", c " + details.size()   +
                  ")");

//        final int             nextIndex[] = {0};
//        final IndexedStrength rivers   [] =
//                new IndexedStrength[ nRivers ];
//        RiverEvalLookup.traverse(
//                toBucketize,
//                new RiverEvalLookup.VsRandomVisitor() {
//                    public void traverse(
//                            long   canonIndex,
//                            double strengthVsRandom,
//                            byte   represents) {
//
//                        rivers[ nextIndex[0]++ ] = new IndexedStrength(
//                                canonIndex, strengthVsRandom);
//                    }
//                });
//        for (CanonRange riverRange : toBucketize) {
//            for (long river  = riverRange.from();
//                      river <= riverRange.toInclusive();
//                      river++) {
//                rivers[ nextIndex[0]++ ] = new IndexedStrength(
//                        river, MemProbCounts.compactProb( river ));
//            }
//        }
//        Arrays.sort(rivers);

//        BucketError     error = new BucketError( nBuckets );
        BucketAllocator alloc =
                new BucketAllocator(
                        details.size(), (char) nBuckets);
        for (int i = 0; i < details.size(); i++)
        {
            branch.set(details.index(i),
                       alloc.nextBucket(
                               details.represents(i)));
        }

//        for (IndexedStrength river : rivers)
//        {
//            long   index    = river.index();
//            int    bucket   = (int) alloc.nextBucket(1);
//            double strength = river.strength();
//
//            branch.set(river.index(),
//                       (byte) alloc.nextBucket(1));
//        }
        
        return new HandStrengthMeasure().error(branch, nBuckets);
    }


    //--------------------------------------------------------------------
    public void setThorough(boolean highPercision) {}


    //--------------------------------------------------------------------
    public String id()
    {
        return "percent";
    }

    @Override public String toString()
    {
        return id();
    }
}
