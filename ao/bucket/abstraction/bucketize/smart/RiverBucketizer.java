package ao.bucket.abstraction.bucketize.smart;

import ao.bucket.abstraction.access.tree.BucketList;
import ao.bucket.index.canon.river.RiverLookup;
import ao.bucket.index.detail.river.compact.CompactProbabilityCounts;
import ao.bucket.index.detail.river.compact.CompactRiverProbabilities;
import ao.bucket.index.detail.river.compact.MemProbCounts;
import ao.unsupervised.cluster.analysis.KMeans;
import ao.unsupervised.cluster.error.TwoPassWcss;
import ao.unsupervised.cluster.space.impl.ScalarDomain;
import ao.unsupervised.cluster.space.measure.scalar.MeanEuclidean;
import ao.unsupervised.cluster.trial.Clustering;
import ao.unsupervised.cluster.trial.ClusteringTrial;
import ao.unsupervised.cluster.trial.ParallelTrial;
import org.apache.log4j.Logger;

/**
 * User: alex
 * Date: 2-Jun-2009
 * Time: 4:00:31 PM
 */
public class RiverBucketizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(RiverBucketizer.class);

    public static void main(String[] args) {
        RiverBucketizer.bucketizeAll((byte) 5, null);
    }


    //--------------------------------------------------------------------
    public RiverBucketizer() {}


    //--------------------------------------------------------------------
    public static double bucketize(
            final int        parentTurns[],
            final BucketList into,
            final byte       nClusters)
    {
        LOG.debug("bucketize");
        return Double.NaN;
    }


    //--------------------------------------------------------------------
    public static double bucketizeAll(
            final byte       nClusters,
            final BucketList into)
    {
        LOG.debug("bucketizeAll");

        ScalarDomain<MeanEuclidean> domain =
                new ScalarDomain<MeanEuclidean>(
                        MeanEuclidean.newFactory());
        for (char prob = 0;
                  prob < CompactRiverProbabilities.COUNT;
                  prob++) {
            domain.add(prob, CompactProbabilityCounts.countOf(prob));
        }

        final Clustering clustering = cluster(domain, nClusters);
        final byte       clusters[] = clustering.clusters();

        LOG.debug("apply clusters");
        for (long river = 0; river < RiverLookup.CANONS; river++)
        {
            into.set(river,
                     clusters[ MemProbCounts.compactProb(river) ]);
        }
//        RiverEvalLookup.traverse(
//            new CanonRange[]{new CanonRange(0, RiverLookup.CANONS)},
//            new RiverEvalLookup.VsRandomVisitor() {
//                public void traverse(
//                        long   canonIndex,
//                        double strengthVsRandom,
//                        byte   represents) {
//                    char compactStr =
//                            CompactRiverProbabilities
//                                    .compact(strengthVsRandom);
//                    into.set(canonIndex, clusters[compactStr]);
//                }
//            });

        LOG.debug("done: " + clustering.error());
        return clustering.error();
    }



    //--------------------------------------------------------------------
    private static Clustering cluster(
            ScalarDomain<MeanEuclidean> domain,
            byte                        nClusters)
    {
        LOG.debug("clustering");

        domain.normalize();

        ClusteringTrial<MeanEuclidean> analyzer =
                new ParallelTrial<MeanEuclidean>(
                        new KMeans<MeanEuclidean>(),
                        new TwoPassWcss<MeanEuclidean>(),
                        512);
        Clustering clusters = analyzer.cluster(domain, nClusters);
        analyzer.close();
        return clusters;
    }
}
