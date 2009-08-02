package ao.bucket.abstraction.bucketize.smart;

import ao.bucket.abstraction.access.tree.LongByteList;
import ao.bucket.index.canon.river.RiverLookup;
import ao.bucket.index.detail.range.CanonRange;
import ao.bucket.index.detail.range.RangeLookup;
import ao.bucket.index.detail.river.compact.CompactProbabilityCounts;
import ao.bucket.index.detail.river.compact.CompactRiverProbabilities;
import ao.bucket.index.detail.river.compact.MemProbCounts;
import ao.holdem.model.Round;
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
            LongByteList branch,
            Round        parentRound,
            int          parentCanons[],
            byte         nClusters)
    {
        LOG.debug("retrieve data");
        int probCount[] = new int[ CompactRiverProbabilities.COUNT ];
        for (CanonRange rivers : RangeLookup.lookup(
                parentRound, parentCanons, Round.RIVER)) {
            for (long river  = rivers.from();
                      river <= rivers.toInclusive();
                      river++)
            {
                probCount[ MemProbCounts.compactProb(river) ]+=
                        MemProbCounts.compactCount(river);
            }
        }

        LOG.debug("encoding clustering domain");
        ScalarDomain<MeanEuclidean> domain =
                new ScalarDomain<MeanEuclidean>(
                        MeanEuclidean.newFactory());
        for (int probability = 0;
                 probability < probCount.length;
                 probability++)
        {
            int count = probCount[ probability ];

            if (count > 0)
            {
                domain.add(probability, count);
            }
        }

        Clustering clustering = cluster(domain, nClusters);
        byte       clusters[] = clustering.clusters();

        LOG.debug("apply clusters");
        for (CanonRange rivers : RangeLookup.lookup(
                parentRound, parentCanons, Round.RIVER)) {
            for (long river  = rivers.from();
                      river <= rivers.toInclusive();
                      river++)
            {
                branch.set(river, clusters[
                        MemProbCounts.compactProb(river) ]);

                probCount[ MemProbCounts.compactProb(river) ]+=
                        MemProbCounts.compactCount(river);
            }
        }

        LOG.debug("sorting clusters");
        BucketSort.sortRiverBranch(
                branch, parentRound, parentCanons, nClusters);

        LOG.debug("done: " + clustering.error());
        return clustering.error();
    }


    //--------------------------------------------------------------------
    public static double bucketizeAll(
            final byte       nClusters,
            final LongByteList into)
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


        LOG.debug("sorting clusters");
        BucketSort.sortRiver(into, nClusters);

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
