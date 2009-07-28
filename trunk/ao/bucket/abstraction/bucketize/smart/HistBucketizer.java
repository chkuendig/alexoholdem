package ao.bucket.abstraction.bucketize.smart;

import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.access.tree.LongByteList;
import ao.bucket.abstraction.access.tree.list.FullLongByteList;
import ao.bucket.abstraction.bucketize.def.Bucketizer;
import ao.bucket.index.canon.Canons;
import ao.bucket.index.canon.hole.HoleLookup;
import ao.bucket.index.canon.river.RiverLookup;
import ao.bucket.index.detail.CanonRange;
import ao.bucket.index.detail.DetailLookup;
import ao.bucket.index.detail.preflop.HoleOdds;
import ao.holdem.model.Round;
import ao.unsupervised.cluster.analysis.KMeans;
import ao.unsupervised.cluster.error.TwoPassWcss;
import ao.unsupervised.cluster.space.impl.CentroidDomain;
import ao.unsupervised.cluster.space.measure.Centroid;
import ao.unsupervised.cluster.space.measure.vector.VectorEuclidean;
import ao.unsupervised.cluster.trial.Clustering;
import ao.unsupervised.cluster.trial.ClusteringTrial;
import ao.unsupervised.cluster.trial.SerialTrial;
import ao.util.data.Arr;
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

        byte nHoleBuckets = 16;
        new HistBucketizer().bucketizeHoles(
                holeBuckets,
                nHoleBuckets,
                (byte) 10);

        for (byte bucket = 0; bucket < nHoleBuckets; bucket++) {
            for (int i = 0, j = HoleLookup.CANONS - 1;
                     i < HoleLookup.CANONS; i++, j--) {
                if (bucket != holeBuckets.get(i)) continue;
                System.out.print(
                        HoleLookup.lookup(j) + " ");
            }
            System.out.println();
        }
    }


    //--------------------------------------------------------------------
    public HistBucketizer()
    {

    }


    //--------------------------------------------------------------------


    //--------------------------------------------------------------------
    public double bucketize(BucketTree.Branch branch, byte numBuckets) {
        if (branch.round() == Round.PREFLOP) {
            return bucketizeHoles(branch, numBuckets, (byte) 10);
        }

        if (branch.round() == Round.RIVER)
        {
//            RiverBucketizer.bucketize();
        }


        return 0;
    }


    //--------------------------------------------------------------------
    private double bucketize(
            BucketTree.Branch branch,
            byte              nBuckets,
            byte              nRiverHist)
    {
//        LongByteList riverBuckets =
//                new HalfLongByteList(null, RiverLookup.CANONS);
//        RiverBucketizer.bucketizeAll(nRiverHist, riverBuckets);
//
//        CentroidDomain<Centroid<double[]>, double[]> byNextRound =
//                allByRiver(Round.PREFLOP, riverBuckets, nRiverHist);
//
//        ClusteringTrial<Centroid<double[]>> analyzer =
//                new ParallelTrial<Centroid<double[]>>(
//                        new KMeans<Centroid<double[]>>(),
//                        new TwoPassWcss<Centroid<double[]>>(),
//                        512);
//
//        Clustering clustering =
//                analyzer.cluster(byNextRound, nHoleBuckets);
//
//        for (int i = 0; i < HoleLookup.CANONS; i++) {
//            preflopBranch.set(
//                    i, clustering.cluster(i));
//        }
//
//        return clustering.error();
        return Double.NaN;
    }


    //--------------------------------------------------------------------
    private double bucketizeHoles(
            LongByteList preflopBranch,
            byte       nHoleBuckets,
            byte       nRiverHist)
    {
        LongByteList riverBuckets =
                new FullLongByteList(null, RiverLookup.CANONS);
        RiverBucketizer.bucketizeAll(nRiverHist, riverBuckets);

        CentroidDomain<Centroid<double[]>, double[]> byFutureRound =
                allByRiver(Round.PREFLOP, riverBuckets, nRiverHist);

//        CentroidDomain<Centroid<double[]>, double[]> byFutureRound =
//        PersistentObjects.retrieve(
//                "/home/alex/proj/datamine/input/holeDomain.obj");

        ClusteringTrial<Centroid<double[]>> analyzer =
                new SerialTrial<Centroid<double[]>>(
                        new KMeans<Centroid<double[]>>(),
                        new TwoPassWcss<Centroid<double[]>>(),
                        1);
        Clustering clustering =
                analyzer.cluster(byFutureRound, nHoleBuckets);
        analyzer.close();

        for (int i = 0; i < HoleLookup.CANONS; i++) {
            preflopBranch.set(
                    i, clustering.cluster(i));
        }

        BucketSort.sortPreFlop(preflopBranch, nHoleBuckets);

        return clustering.error();
    }


    //--------------------------------------------------------------------
    private CentroidDomain<Centroid<double[]>, double[]>
            allByRiver(
                    Round        thisRound,
                    LongByteList riverBuckets,
                    byte         nRiverBuckets)
    {
        LOG.debug("building full domain for " + thisRound);

        CentroidDomain<Centroid<double[]>, double[]> byFutureRound =
                new CentroidDomain<Centroid<double[]>, double[]>(
                        VectorEuclidean.newFactory(nRiverBuckets)
//                        Mahalanobis.newFactory(nRiverBuckets)
//                        VectorInfo.newFactory(nRiverBuckets)
                        , Equalizers.doubleArray()
                );

        // order of for loop (high -> low) must be consistent, see above
        for (int canon = (int)(Canons.count(thisRound) - 1);
                 canon >= 0;
                 canon--) {

            double hist    [] = futureRoundHist(
                    thisRound, Round.RIVER,
                    canon, riverBuckets, nRiverBuckets);
            double normHist[] = Info.normalize(hist);

            byFutureRound.add(
                    normHist, sum(hist));

            System.out.println(
                    HoleLookup.lookup(canon) + "\t" +
                    HoleOdds.lookup(canon).strengthVsRandom() + "\t" +
                    Arr.join(normHist, "\t"));
        }
        //byFutureRound.normalize();

//        PersistentObjects.persist(byFutureRound,
//                "/home/alex/proj/datamine/input/holeDomain.obj");

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
            Round      thisRound,
            Round      futureRound,
            int        forCanon,
            LongByteList futureRoundBuckets,
            byte       nFutureRoundBuckets)
    {
        double     histogram[]      = new double[ nFutureRoundBuckets ];
        CanonRange futureRoundRange =
                DetailLookup.lookupRange(
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
