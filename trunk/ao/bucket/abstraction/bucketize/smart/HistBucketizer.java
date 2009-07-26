package ao.bucket.abstraction.bucketize.smart;

import ao.bucket.abstraction.access.tree.BucketList;
import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.access.tree.list.BucketListImpl;
import ao.bucket.abstraction.access.tree.list.HalfBucketList;
import ao.bucket.abstraction.bucketize.def.Bucketizer;
import ao.bucket.index.canon.Canons;
import ao.bucket.index.canon.hole.HoleLookup;
import ao.bucket.index.canon.river.RiverLookup;
import ao.bucket.index.detail.CanonRange;
import ao.bucket.index.detail.DetailLookup;
import ao.holdem.model.Round;
import ao.unsupervised.cluster.analysis.KMeans;
import ao.unsupervised.cluster.error.TwoPassWcss;
import ao.unsupervised.cluster.space.impl.CentroidDomain;
import ao.unsupervised.cluster.space.measure.Centroid;
import ao.unsupervised.cluster.space.measure.vector.VectorEuclidean;
import ao.unsupervised.cluster.trial.Clustering;
import ao.unsupervised.cluster.trial.ClusteringTrial;
import ao.unsupervised.cluster.trial.ParallelTrial;
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
        BucketList holeBuckets =
                new BucketListImpl(null, HoleLookup.CANONS);

        byte nHoleBuckets = 32;
        new HistBucketizer().bucketizeHoles(
                holeBuckets,
                nHoleBuckets,
                (byte) 10);

        for (byte bucket = 0; bucket < nHoleBuckets; bucket++) {
            for (int i = 0; i < HoleLookup.CANONS; i++) {
                if (bucket != holeBuckets.get(i)) continue;
                System.out.print(
                        HoleLookup.lookup(i) + " ");
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
//        BucketList riverBuckets =
//                new HalfBucketList(null, RiverLookup.CANONS);
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
            BucketList preflopBranch,
            byte       nHoleBuckets,
            byte       nRiverHist)
    {
        BucketList riverBuckets =
                new HalfBucketList(null, RiverLookup.CANONS);
        RiverBucketizer.bucketizeAll(nRiverHist, riverBuckets);

        CentroidDomain<Centroid<double[]>, double[]> byNextRound =
                allByRiver(Round.PREFLOP, riverBuckets, nRiverHist);

        ClusteringTrial<Centroid<double[]>> analyzer =
                new ParallelTrial<Centroid<double[]>>(
                        new KMeans<Centroid<double[]>>(),
                        new TwoPassWcss<Centroid<double[]>>(),
                        512);

        Clustering clustering =
                analyzer.cluster(byNextRound, nHoleBuckets);

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
                    Round      thisRound,
                    BucketList riverBuckets,
                    byte       nRiverBuckets)
    {
        LOG.debug("building full domain for " + thisRound);

        CentroidDomain<Centroid<double[]>, double[]> byNextRound =
                new CentroidDomain<Centroid<double[]>, double[]>(
                        VectorEuclidean.newFactory(nRiverBuckets)
//                        Mahalanobis.newFactory(nRiverBuckets)
                        , Equalizers.doubleArray()
                );

        // order of for loop (high -> low) must be consistent, see above
        for (int canon = (int)(Canons.count(thisRound) - 1);
                 canon >= 0;
                 canon--) {

            double hist[] = futureRoundHist(thisRound, Round.RIVER,
                    canon, riverBuckets, nRiverBuckets);
            byNextRound.add(hist, 1);
        }
        //byFutureRound.normalize();

        LOG.debug("done");
        return byNextRound;
    }


    //--------------------------------------------------------------------
    private double[] futureRoundHist(
            Round      thisRound,
            Round      futureRound,
            int        forCanon,
            BucketList futureRoundBuckets,
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
