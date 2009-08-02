package ao.bucket.abstraction.bucketize.smart;

import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.access.tree.LongByteList;
import ao.bucket.abstraction.access.tree.list.FullLongByteList;
import ao.bucket.abstraction.bucketize.def.Bucketizer;
import ao.bucket.index.canon.hole.HoleLookup;
import ao.bucket.index.canon.river.RiverLookup;
import ao.bucket.index.detail.DetailLookup;
import ao.bucket.index.detail.range.CanonRange;
import ao.bucket.index.detail.range.RangeLookup;
import ao.holdem.model.Round;
import ao.unsupervised.cluster.analysis.KMeans;
import ao.unsupervised.cluster.error.TwoPassWcss;
import ao.unsupervised.cluster.space.impl.CentroidDomain;
import ao.unsupervised.cluster.space.measure.Centroid;
import ao.unsupervised.cluster.space.measure.vector.VectorEuclidean;
import ao.unsupervised.cluster.trial.Clustering;
import ao.unsupervised.cluster.trial.ClusteringTrial;
import ao.unsupervised.cluster.trial.SerialTrial;
import ao.util.math.stats.Info;
import ao.util.misc.Equalizers;
import org.apache.log4j.Logger;

/**
 * User: aostrovsky
 * Date: 25-Jul-2009
 * Time: 11:42:15 AM
 */
public class HistBucketizer implements Bucketizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(HistBucketizer.class);

    public static void main(String[] args) {
        LongByteList holeBuckets =
                new FullLongByteList(null, HoleLookup.CANONS);

        byte nHoleBuckets = 32;
        new HistBucketizer().bucketizePreRiver(
                holeBuckets,
                Round.PREFLOP,
                new int[0],
                nHoleBuckets,
                (byte) 4);

        BucketDisplay.displayHoleBuckets(holeBuckets);
    }


    //--------------------------------------------------------------------
    private final LongByteList riverBuckets =
                new FullLongByteList(null, RiverLookup.CANONS);


    //--------------------------------------------------------------------
    public HistBucketizer()
    {

    }


    //--------------------------------------------------------------------
    public double bucketize(BucketTree.Branch branch, byte numBuckets) {
        switch (branch.round())
        {
            case RIVER:
                return RiverBucketizer.bucketize(
                    branch, Round.TURN,
                    branch.parentCanons(), numBuckets);

            case PREFLOP:
                double error = bucketizePreRiver(
                    branch, branch.round(),
                    new int[]{-1},
                    numBuckets, (byte) 3);
                BucketSort.sortPreFlop(branch, numBuckets);
                return error;

            default:
                return bucketizePreRiver(
                    branch, branch.round(),
                    branch.parentCanons(),
                    numBuckets, (byte) 3);
        }
    }


    //--------------------------------------------------------------------
    private double bucketizePreRiver(
            LongByteList branch,
            Round        round,
            int          parents[],
            byte         nBuckets,
            byte         nRiverHist)
    {
        RiverBucketizer.bucketize(
                riverBuckets, round.previous(), parents, nRiverHist);

        CentroidDomain<Centroid<double[]>, double[]> byFutureRound =
                byRiver(round, parents, nRiverHist);

        ClusteringTrial<Centroid<double[]>> analyzer =
                new SerialTrial<Centroid<double[]>>(
                        new KMeans<Centroid<double[]>>(),
                        new TwoPassWcss<Centroid<double[]>>(),
                        1);
        Clustering clustering =
                analyzer.cluster(byFutureRound, nBuckets);
        analyzer.close();

        int clusterIndex = 0;
        for (CanonRange canons : RangeLookup.lookup(
                round.previous(), parents, round)) {
            for (int canon  = (int) canons.from();
                     canon <= canons.toInclusive();
                     canon++)
            {
                branch.set(canon,
                           clustering.cluster( clusterIndex++ ));
            }
        }

        BucketSort.sortPreFlop(branch, nBuckets);

        return clustering.error();
    }


    //--------------------------------------------------------------------
    private CentroidDomain<Centroid<double[]>, double[]>
            byRiver(Round        round,
                    int          parents[],
                    byte         nRiverBuckets)
    {
        LOG.debug("building domain for " + round);

        CentroidDomain<Centroid<double[]>, double[]> byFutureRound =
                new CentroidDomain<Centroid<double[]>, double[]>(
                        VectorEuclidean.newFactory(nRiverBuckets)
//                        Mahalanobis.newFactory(nRiverBuckets)
//                        VectorInfo.newFactory(nRiverBuckets)
                        , Equalizers.doubleArray()
                );

        for (CanonRange canons : RangeLookup.lookup(
                round.previous(), parents, round)) {
            for (int canon = (int) canons.from();
                     canon <= canons.toInclusive();
                     canon++) {

                double hist    [] = futureRoundHist(
                        round, Round.RIVER, canon,
                        riverBuckets, nRiverBuckets);
                double normHist[] = Info.normalize(hist);

                byFutureRound.add(
                        normHist, sum(hist));
            }
        }

        LOG.debug("done");
        return byFutureRound;
    }

    private int sum(double hist[])
    {
        double sum = 0;
        for (double val : hist) {
            sum += val;
        }
        return (int) Math.round(sum);
    }


    //--------------------------------------------------------------------
    private double[] futureRoundHist(
            Round        thisRound,
            Round        futureRound,
            int          forCanon,
            LongByteList futureRoundBuckets,
            byte         nFutureRoundBuckets)
    {
        double     histogram[]      = new double[ nFutureRoundBuckets ];
        CanonRange futureRoundRange =
                RangeLookup.lookupRange(
                        forCanon, thisRound, futureRound);
        for (long canon  = futureRoundRange.from();
                  canon <= futureRoundRange.toInclusive();
                  canon++)
        {
            histogram[ futureRoundBuckets.get(canon) ] +=
                    DetailLookup.lookupRepresentation(
                            futureRound, canon);
        }
        return histogram;
    }


    //--------------------------------------------------------------------
    public String id() {
        return "Potential";
    }
}
