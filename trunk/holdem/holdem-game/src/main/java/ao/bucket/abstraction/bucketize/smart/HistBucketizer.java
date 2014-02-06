package ao.bucket.abstraction.bucketize.smart;

import ao.ai.cluster.analysis.KMeans;
import ao.ai.cluster.error.TwoPassWcss;
import ao.ai.cluster.space.impl.CentroidDomain;
import ao.ai.cluster.space.measure.Centroid;
import ao.ai.cluster.space.measure.vector.VectorEuclidean;
import ao.ai.cluster.trial.Clustering;
import ao.ai.cluster.trial.ClusteringTrial;
import ao.ai.cluster.trial.ParallelTrial;
import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.access.tree.LongByteList;
import ao.bucket.abstraction.access.tree.list.FullLongByteList;
import ao.bucket.abstraction.access.tree.list.HalfLongByteList;
import ao.bucket.abstraction.bucketize.def.Bucketizer;
import ao.bucket.index.canon.hole.CanonHole;
import ao.bucket.index.canon.river.River;
import ao.bucket.index.detail.DetailLookup;
import ao.bucket.index.detail.range.CanonRange;
import ao.bucket.index.detail.range.RangeLookup;
import ao.holdem.model.Round;
import ao.util.data.Arrs;
import ao.util.math.stats.Info;
import ao.util.misc.Equalizers;
import ao.util.time.Stopwatch;
import org.apache.log4j.Logger;

/**
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
                new FullLongByteList(null, CanonHole.CANONS);

        byte nHoleBuckets = 127;
        for (byte dim = 2; dim <= nHoleBuckets; dim++)
        {
            LOG.debug("clustering " + dim);
            new HistBucketizer().bucketizePreRiver(
                holeBuckets,
                Round.PREFLOP,
                new int[0],
                nHoleBuckets,
                dim);
            BucketDisplay.displayHoleBuckets(holeBuckets);
        }
//        new HistBucketizer().bucketizePreRiver(
//                holeBuckets,
//                Round.PREFLOP,
//                new int[0],
//                nHoleBuckets,
//                (byte) 4);
//        BucketDisplay.displayHoleBuckets(holeBuckets);
    }


    //--------------------------------------------------------------------
    private final LongByteList riverBuckets =
                new HalfLongByteList(null, River.CANONS);

    private final byte[]       nDimensions;

    private       boolean      isThorough;


    //--------------------------------------------------------------------
    public HistBucketizer()
    {
        this((byte) 3);
    }

    public HistBucketizer(byte dim)
    {
        this(dim, dim, dim);
    }

    public HistBucketizer(
            byte holeDim, byte flopDim, byte turnDim)
    {
        nDimensions = new byte[]{
                holeDim, flopDim, turnDim};
    }


    //--------------------------------------------------------------------
    public void setThorough(boolean highPrecision) {
        isThorough = highPrecision;
    }


    //--------------------------------------------------------------------
    public double bucketize(BucketTree.Branch branch, int numBuckets) {
        switch (branch.round())
        {
            case RIVER:
                return RiverBucketizer.bucketize(
                        branch, Round.TURN,
                        branch.parentCanons(), numBuckets);

            case PREFLOP:
                double error = bucketizePreRiver(
                        branch, branch.round(),
                        new int[0],
                        numBuckets, nDimensions[0]);
                BucketSort.sortPreFlop(branch, numBuckets);
                return error;

            default:
                return bucketizePreRiver(
                        branch, branch.round(),
                        branch.parentCanons(), numBuckets,
                        nDimensions[ branch.round().ordinal() ]);
        }
    }


    //--------------------------------------------------------------------
    private double bucketizePreRiver(
            LongByteList branch,
            Round        round,
            int          parents[],
            int          nBuckets,
            byte         nRiverHist)
    {
        LOG.trace("bucketizePreRiver" +
                    " round " + round +
                    ", |parents| " + parents.length +
                    ", nBuckets " + nBuckets +
                    ", nRiverHist " + nRiverHist);
        Stopwatch timer = new Stopwatch();

        RiverBucketizer.bucketize(
                riverBuckets, round.previous(), parents, nRiverHist);

        CentroidDomain<Centroid<double[]>, double[]> byFutureRound =
                byRiver(round, parents, nRiverHist);

        double error = cluster(
                 branch, round, parents, nBuckets, byFutureRound
               ).error();

        LOG.debug("bucketizePreRiver" +
                    " round " + round +
                    ", |parents| " + parents.length +
                    ", nBuckets " + nBuckets +
                    ", nRiverHist " + nRiverHist +
                    ", error " + error +
                    ", took " + timer);
        return error;
    }

    private Clustering cluster(
            LongByteList                                 branch,
            Round                                        round,
            int[]                                        parents,
            int                                          nBuckets,
            CentroidDomain<Centroid<double[]>, double[]> byFutureRound)
    {
        LOG.trace("clustering" +
                    " round " + round +
                    ", |parents| " + parents.length +
                    ", nBuckets " + nBuckets);
        Stopwatch timer = new Stopwatch();

        ClusteringTrial<Centroid<double[]>> analyzer =
                new ParallelTrial<Centroid<double[]>>(
                        new KMeans<Centroid<double[]>>(),
                        new TwoPassWcss<Centroid<double[]>>(),
                        (isThorough ? 512 : 8));
        Clustering clustering =
                analyzer.cluster(byFutureRound, nBuckets);
        analyzer.close();

        LOG.trace("applying clusters");
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

        if (round == Round.PREFLOP) {
            LOG.trace("sorting clusters");
            BucketSort.sortPreFlop(branch, nBuckets);
        }
        
        LOG.trace("done clustering with " + clustering.error() +
                    ", took " + timer);
        return clustering;
    }


    //--------------------------------------------------------------------
    private CentroidDomain<Centroid<double[]>, double[]>
            byRiver(Round        round,
                    int          parents[],
                    byte         nRiverBuckets)
    {
        LOG.trace("building domain for " + round +
                  " with " + nRiverBuckets +
                  " from " + parents.length);
        Stopwatch timer = new Stopwatch();

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

        LOG.trace("done building domain, took " + timer);
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
        return "Hist." + Arrs.join(nDimensions, ".");
    }
}
