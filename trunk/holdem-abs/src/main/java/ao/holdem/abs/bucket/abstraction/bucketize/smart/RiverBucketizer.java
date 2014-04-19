package ao.holdem.abs.bucket.abstraction.bucketize.smart;

//import ao.ai.cluster.analysis.KMeans;
//import ao.ai.cluster.error.TwoPassWcss;
//import ao.ai.cluster.space.impl.ScalarDomain;
//import ao.ai.cluster.space.measure.scalar.MeanEuclidean;
//import ao.ai.cluster.trial.Clustering;
//import ao.ai.cluster.trial.ClusteringTrial;
//import ao.ai.cluster.trial.ParallelTrial;
import ao.holdem.abs.bucket.abstraction.access.tree.LongByteList;
import ao.holdem.abs.bucket.index.detail.range.CanonRange;
import ao.holdem.abs.bucket.index.detail.range.RangeLookup;
import ao.holdem.abs.bucket.index.detail.river.ProbabilityEncoding;
import ao.holdem.abs.bucket.index.detail.river.compact.CompactRiverProbabilities;
import ao.holdem.abs.bucket.index.detail.river.compact.MemProbCounts;
import ao.holdem.model.Round;
import com.google.common.base.Stopwatch;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.clustering.MultiKMeansPlusPlusClusterer;
import org.apache.log4j.Logger;

/**
 * 2-Jun-2009
 */
public class RiverBucketizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(RiverBucketizer.class);

//    public static void main(String[] args) {
//        RiverBucketizer.bucketizeAll((byte) 5, null);
//    }


    //--------------------------------------------------------------------
    public RiverBucketizer() {}


    //--------------------------------------------------------------------
//    public static double bucketize(
//            LongByteList branch,
//            Round        parentRound,
//            int          parentCanons[],
//            int          nClusters)
//    {
//        LOG.trace("bucketizing " +
//                    " parentRound " + parentRound +
//                    ", |parentCanons| " + parentCanons.length +
//                    ", nClusters " + nClusters);
//        Stopwatch timer = Stopwatch.createStarted();
//
//        int probCount[] = new int[ ProbabilityEncoding.COUNT ];
//        for (CanonRange rivers : RangeLookup.lookup(
//                parentRound, parentCanons, Round.RIVER))
//        {
//            for (long river  = rivers.from();
//                      river <= rivers.toInclusive();
//                      river++)
//            {
//                probCount[ MemProbCounts.compactNonLossProbability(river) ]+= MemProbCounts.compactCount(river);
//            }
//        }
//
//        LOG.trace("encoding clustering domain");
//        ScalarDomain<MeanEuclidean> domain =
//                new ScalarDomain<MeanEuclidean>(
//                        MeanEuclidean.newFactory());
//
//        int[] domainIndexes   = new int[ probCount.length ];
//        int   nextDomainIndex = 0;
//        for (int probability = 0;
//                 probability < probCount.length;
//                 probability++)
//        {
//            int count = probCount[ probability ];
//
//            if (count > 0)
//            {
//                domain.add(ProbabilityEncoding.decodeWinProb(
//                        (char) probability), count);
//                domainIndexes[ probability ] = nextDomainIndex++;
//            }
//        }
//
//        Clustering clustering = cluster(domain, nClusters);
//        int        clusters[] = clustering.clusters();
//
//        LOG.trace("apply clusters");
//        for (CanonRange rivers : RangeLookup.lookup(
//                parentRound, parentCanons, Round.RIVER)) {
//            for (long river  = rivers.from();
//                      river <= rivers.toInclusive();
//                      river++)
//            {
//                branch.set(river, clusters[domainIndexes[
//                        CompactRiverProbabilities.nonLossProbability(
//                                MemProbCounts.compactProb(river)) ]]);
//            }
//        }
//
////        LOG.trace("sorting clusters");
////        BucketSort.sortRiverBranch(
////                branch, parentRound, parentCanons, nClusters);
//
//        LOG.trace("bucketized " +
//                    " parentRound " + parentRound +
//                    ", |parentCanons| " + parentCanons.length +
//                    ", nClusters " + nClusters +
//                    ", err " + clustering.error() +
//                    ", took " + timer);
//        return clustering.error();
//    }


    //--------------------------------------------------------------------
//    public static double bucketizeAll(
//            final byte         nClusters,
//            final LongByteList into)
//    {
//        return bucketize(into, null, new int[0], nClusters);
//    }



//    //--------------------------------------------------------------------
//    private static Clustering cluster(
//            ScalarDomain<MeanEuclidean> domain,
//            int                         nClusters)
//    {
//        LOG.trace("clustering");
//
//        domain.normalize();
//
//        ClusteringTrial<MeanEuclidean> analyzer =
//                new ParallelTrial<MeanEuclidean>(
//                        new KMeans<MeanEuclidean>(),
//                        new TwoPassWcss<MeanEuclidean>(),
//                        512);
//        Clustering clusters = analyzer.cluster(domain, nClusters);
//        analyzer.close();
//        return clusters;
//    }
}
