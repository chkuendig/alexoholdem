package ao.bucket.abstraction.bucketize.smart;

import ao.bucket.abstraction.access.tree.BucketList;
import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.access.tree.list.HalfBucketList;
import ao.bucket.abstraction.bucketize.def.Bucketizer;
import ao.bucket.index.canon.river.RiverLookup;
import ao.bucket.index.canon.turn.TurnLookup;
import ao.bucket.index.detail.CanonRange;
import ao.bucket.index.detail.river.compact.MemProbCounts;
import ao.bucket.index.detail.turn.TurnRivers;
import ao.unsupervised.cluster.analysis.KMeans;
import ao.unsupervised.cluster.error.TwoPassWcss;
import ao.unsupervised.cluster.space.impl.CentroidDomain;
import ao.unsupervised.cluster.space.measure.Centroid;
import ao.unsupervised.cluster.space.measure.vector.Mahalanobis;
import ao.unsupervised.cluster.trial.Clustering;
import ao.unsupervised.cluster.trial.ClusteringTrial;
import ao.unsupervised.cluster.trial.ParallelTrial;
import ao.util.data.AutovivifiedMap;
import ao.util.data.primitive.DoubleList;
import ao.util.misc.Factories;
import ao.util.time.Progress;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * User: alex
 * Date: 5-Jul-2009
 * Time: 5:15:33 PM
 */
public class PotentialBucketizer implements Bucketizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(PotentialBucketizer.class);

    public static void main(String[] args) {
        new PotentialBucketizer().bucketizeAll(
                null,
                (byte) 16,
                (byte) 10,
                (byte) 5,
                (byte) 5);
    }


    //--------------------------------------------------------------------
    public PotentialBucketizer()
    {

    }


    //--------------------------------------------------------------------


    //--------------------------------------------------------------------
    public double bucketize(BucketTree.Branch branch, byte numBuckets) {
//        if (branch.round() == Round.PREFLOP) {
//            return bucketizeAll(branch, numBuckets);
//        }

        return 0;
    }


    //--------------------------------------------------------------------
    private double bucketizeAll(
            BucketTree.Branch preflopBranch,
            byte              nHoleBuckets,
            byte              nFlopBuckets,
            byte              nTurnBuckets,
            byte              nRiverBuckets)
    {
        BucketList riverBuckets = new HalfBucketList(
                null, RiverLookup.CANONS);
        RiverBucketizer.bucketizeAll(
                        nRiverBuckets, riverBuckets);

        CentroidDomain<Centroid<double[]>, double[]>
                turnDomain = byRiver(riverBuckets, nRiverBuckets);
        for (byte t = 1; t < 30; t++)
        {
            BucketList turnBuckets = new HalfBucketList(
                    null, TurnLookup.CANONS);
            double err = bucketizerAllTurns(
                    turnDomain, turnBuckets,
                    t /*nTurnBuckets*/);
            System.out.println(err);
        }


//        switch (branch.round())
//        {
//            case RIVER: {
//                return RiverBucketizer.bucketizeAll(
//                        nBuckets, branch);
//            }
//
//            case TURN: {
//                return bucketizerAllTurns(branch, nBuckets);
//            }
//        }

        return Double.NaN;
    }

    //--------------------------------------------------------------------
    private double bucketizerAllTurns(
            CentroidDomain<Centroid<double[]>, double[]>
                       byRiver,
            BucketList turnBuckets,
            byte       nTurnBuckets)
    {
        LOG.debug("bucketizerAllTurns");
        ClusteringTrial<Centroid<double[]>> analyzer =
                new ParallelTrial<Centroid<double[]>>(
                        new KMeans<Centroid<double[]>>(),
                        new TwoPassWcss<Centroid<double[]>>(),
                        512);
        Clustering clusters = analyzer.cluster(
                                         byRiver, nTurnBuckets);
        analyzer.close();


        LOG.debug("applying buckets");
//        for (int canonTurn = 0;
//                 canonTurn < TurnLookup.CANONS;
//                 canonTurn++) {
//            turnBuckets.set(
//                    canonTurn,
//                    clusters.cluster(canonTurn));
//        }

        LOG.debug("done");
        return clusters.error();
    }

    private CentroidDomain<Centroid<double[]>, double[]>
            byRiver(BucketList riverBuckets,
                    byte       nRiverBuckets)
    {
        LOG.debug("building turn domain");

        @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
        AutovivifiedMap<DoubleList, int[]> histCount =
                new AutovivifiedMap<DoubleList,int[]>(
                        Factories.newArrayClone(new int[1]));

        Progress progress = new Progress(TurnLookup.CANONS);
        for (int canonTurn = 0;
                 canonTurn < TurnLookup.CANONS;
                 canonTurn++) {
            DoubleList riverHist =
                    riverHist(canonTurn, riverBuckets, nRiverBuckets);
            histCount.get( riverHist )[ 0 ]++;
            progress.checkpoint();
        }

        LOG.debug("applying domain");
        CentroidDomain<Centroid<double[]>, double[]> byRiver =
                new CentroidDomain<Centroid<double[]>, double[]>(
//                        VectorEuclidean.newFactory(nRiverBuckets)
                        Mahalanobis.newFactory(nRiverBuckets)
                );
        for (Map.Entry<DoubleList, int[]> histogram :
                histCount.entrySet()) {
            byRiver.add(histogram.getKey().toArray(),
                        histogram.getValue()[ 0 ]);
        }
        byRiver.normalize();

        LOG.debug("done\t" + histCount.size());
        return byRiver;
    }

    private DoubleList riverHist(
            final int        forTurn,
            final BucketList riverBuckets,
            final byte       nRiverBuckets)
    {
        DoubleList histogram = new DoubleList( nRiverBuckets );

        CanonRange rivers = TurnRivers.rangeOf( forTurn );
        for (long river  = rivers.upToAndIncluding();
                  river >= rivers.fromCanonIndex();
                  river--)
        {
            byte bucket = riverBuckets.get(river);
            histogram.set(bucket,
                    histogram.get(bucket) +
                    MemProbCounts.normRiverCount(river));
        }
        return histogram;
    }


    //--------------------------------------------------------------------
    public String id() {
        return "Potential";
    }
}
