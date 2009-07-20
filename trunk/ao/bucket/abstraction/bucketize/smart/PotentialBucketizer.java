package ao.bucket.abstraction.bucketize.smart;

import ao.bucket.abstraction.access.tree.BucketList;
import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.access.tree.list.BucketListImpl;
import ao.bucket.abstraction.access.tree.list.HalfBucketList;
import ao.bucket.abstraction.bucketize.def.Bucketizer;
import ao.bucket.index.canon.Canons;
import ao.bucket.index.canon.flop.FlopLookup;
import ao.bucket.index.canon.river.RiverLookup;
import ao.bucket.index.canon.turn.TurnLookup;
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
import ao.util.data.AutovivifiedMap;
import ao.util.data.primitive.DoubleList;
import ao.util.misc.Equalizers;
import ao.util.misc.Factories;
import ao.util.misc.Factory;
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
                (byte) 6,
                (byte) 10);
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
        BucketList turnBuckets = turnBuckets(nTurnBuckets, nRiverBuckets);

        CentroidDomain<Centroid<double[]>, double[]>
                flopDomain = byNextRound(
                    Round.FLOP, turnBuckets, nFlopBuckets);
        BucketList flopBuckets = new BucketListImpl(
                null, FlopLookup.CANONS);

        for (byte nBuckets = 1; nBuckets < 30; nBuckets++)
        {
            double err = bucketizeAllPreRiver(
                    Round.FLOP, flopDomain, flopBuckets, turnBuckets,
                    nBuckets /*nFlopBuckets*/, nTurnBuckets);
            System.out.println(nBuckets + "\t" + err);
        }

        return Double.NaN;
    }

    private BucketList turnBuckets(
            byte nTurnBuckets, byte nRiverBuckets) {

        BucketList riverBuckets =
                new HalfBucketList(null, RiverLookup.CANONS);

        CentroidDomain<Centroid<double[]>, double[]>
                turnDomain = turnDomain(nRiverBuckets, riverBuckets);

//        PersistentObjects.persist(turnDomain,
//                "/home/alex/proj/datamine/input/turnDomain.obj");
//        CentroidDomain<Centroid<double[]>, double[]>
//                turnDomain = PersistentObjects.retrieve(
//                    "/home/alex/proj/datamine/input/turnDomain.obj");

        BucketList turnBuckets = new HalfBucketList(
                null, TurnLookup.CANONS);
        bucketizeAllPreRiver(
                Round.TURN, turnDomain,
                turnBuckets, riverBuckets, nTurnBuckets, nRiverBuckets);
        return turnBuckets;
    }

    private CentroidDomain<Centroid<double[]>, double[]> turnDomain(
            byte nRiverBuckets, BucketList riverBuckets) {
//        BucketList riverBuckets =
//                new HalfBucketList(null, RiverLookup.CANONS);
        RiverBucketizer.bucketizeAll(nRiverBuckets, riverBuckets);
        return byNextRound(
                    Round.TURN, riverBuckets, nRiverBuckets);
    }


    //--------------------------------------------------------------------
    private double bucketizeAllPreRiver(
            Round      round,
            CentroidDomain<Centroid<double[]>, double[]>
                       byNextRound,
            BucketList roundBuckets,
            BucketList nextRoundBuckets,
            byte       nRoundBuckets,
            byte       nNextRoundBuckets)
    {
        LOG.debug("bucketizeAllPreRiver " + round);
        ClusteringTrial<Centroid<double[]>> analyzer =
                new ParallelTrial<Centroid<double[]>>(
                        new KMeans<Centroid<double[]>>(),
                        new TwoPassWcss<Centroid<double[]>>(),
                        1 /*512*/);
        Clustering clusters = analyzer.cluster(
                byNextRound, nRoundBuckets);
        analyzer.close();

        LOG.debug("applying buckets");
//        Progress prog = new Progress(Canons.count(round));

        @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
        AutovivifiedMap<DoubleList, Integer> histIndex =
                new AutovivifiedMap<DoubleList, Integer>(
                        new Factory<Integer>() {
                            private int next = 0;
                            public Integer newInstance() {
                                return next++;
                            }
                        });

        // order of for loop (high -> low) must be consistent
        for (int canon  = (int)(Canons.count(round) - 1);
                 canon >= 0;
                 canon--) {
            DoubleList nextRoundHist = nextRoundHist(round, canon,
                    nextRoundBuckets, nNextRoundBuckets);
            roundBuckets.set(
                    canon,
                    clusters.cluster(
                            histIndex.get(nextRoundHist)));

//            prog.checkpoint();
        }

        LOG.debug("done");
        return clusters.error();
    }


    //--------------------------------------------------------------------
    private CentroidDomain<Centroid<double[]>, double[]>
            byNextRound(
                    Round      round,
                    BucketList nextRoundBuckets,
                    byte       nNextRoundBuckets)
    {
        LOG.debug("building " + round + " domain");

        @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
        AutovivifiedMap<DoubleList, int[]> histCount =
                new AutovivifiedMap<DoubleList,int[]>(
                        Factories.newArrayClone(new int[1]));

        // order of for loop (high -> low) must be consistent
//        Progress progress = new Progress(Canons.count( round ));
        for (int canon = (int)(Canons.count( round ) - 1);
                 canon >= 0;
                 canon--) {
            DoubleList nextRoundHist =
                    nextRoundHist(round,
                            canon, nextRoundBuckets, nNextRoundBuckets);
            histCount.get( nextRoundHist )[ 0 ]++;
//            progress.checkpoint();
        }

        LOG.debug("applying domain");
        CentroidDomain<Centroid<double[]>, double[]> byNextRound =
                new CentroidDomain<Centroid<double[]>, double[]>(
                        VectorEuclidean.newFactory(nNextRoundBuckets)
//                        Mahalanobis.newFactory(nRiverBuckets)
                        , Equalizers.doubleArray()
                );
        for (Map.Entry<DoubleList, int[]> histogram :
                histCount.entrySet()) {
            byNextRound.add(
                    histogram.getKey().toArray(),
                    histogram.getValue()[ 0 ]);
        }
        //byNextRound.normalize();

        LOG.debug("done\t" + histCount.size());
        return byNextRound;
    }


    //--------------------------------------------------------------------
    private DoubleList nextRoundHist(
            Round      round,
            int        forCanon,
            BucketList nextRoundBuckets,
            byte       nNextRoundBuckets)
    {
        DoubleList histogram =
                new DoubleList( nNextRoundBuckets, nNextRoundBuckets );

        Round      nextRound      = round.next();
        CanonRange nextRoundRange =
                DetailLookup.lookupRange( round, forCanon );
        for (long canon  = nextRoundRange.upToAndIncluding();
                  canon >= nextRoundRange.fromCanonIndex();
                  canon--)
        {
            histogram.increment(
                    nextRoundBuckets.get(canon),
                    DetailLookup.lookupRepresentation(
                            nextRound, canon));
        }
        return histogram;
    }


    //--------------------------------------------------------------------
    public String id() {
        return "Potential";
    }
}
