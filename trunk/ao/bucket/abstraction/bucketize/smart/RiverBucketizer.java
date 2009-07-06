package ao.bucket.abstraction.bucketize.smart;

import ao.bucket.abstraction.access.tree.BucketList;
import ao.bucket.index.canon.river.RiverLookup;
import ao.bucket.index.detail.CanonRange;
import ao.bucket.index.detail.river.RiverEvalLookup;
import ao.bucket.index.detail.river.compact.CompactProbabilityCounts;
import ao.bucket.index.detail.river.compact.CompactRiverProbabilities;
import ao.unsupervised.cluster.analysis.KMeans;
import ao.unsupervised.cluster.error.TwoPassWcss;
import ao.unsupervised.cluster.space.central_tendency.Mean;
import ao.unsupervised.cluster.space.impl.RealDomain;
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
    public static double bucketizeAll(
            final byte       nClusters,
            final BucketList into)
    {
        LOG.debug("bucketizeAll");

        RealDomain domain = new RealDomain();
        for (char prob = 0;
                  prob < CompactRiverProbabilities.COUNT;
                  prob++) {
            domain.add(prob, CompactProbabilityCounts.countOf(prob));
        }

        final Clustering clustering = cluster(domain, nClusters);
        final byte       clusters[] = clustering.clusters();

        LOG.debug("apply clusters");
        RiverEvalLookup.traverse(
            new CanonRange[]{new CanonRange(0, RiverLookup.CANONS)},
            new RiverEvalLookup.VsRandomVisitor() {
                public void traverse(
                        long   canonIndex,
                        double strengthVsRandom,
                        byte   represents) {
                    char compactStr =
                            CompactRiverProbabilities
                                    .compact(strengthVsRandom);
                    into.set(canonIndex, clusters[compactStr]);
                }
            });

        return clustering.error();
    }



    //--------------------------------------------------------------------
    private static Clustering cluster(
            RealDomain domain,
            byte       nClusters)
    {
        LOG.debug("clustering");

        domain.normalize();

        ClusteringTrial<Mean> analyzer =
                new ParallelTrial<Mean>(
                        new KMeans<Mean>(),
                        new TwoPassWcss<Mean>(),
                        512);
        Clustering clusters = analyzer.cluster(domain, nClusters);
        analyzer.close();
        return clusters;
    }
}
