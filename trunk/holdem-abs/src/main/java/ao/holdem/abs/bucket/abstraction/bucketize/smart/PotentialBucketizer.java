package ao.holdem.abs.bucket.abstraction.bucketize.smart;

//import ao.ai.cluster.analysis.KMeans;
//import ao.ai.cluster.error.TwoPassWcss;
//import ao.ai.cluster.space.impl.CentroidDomain;
//import ao.ai.cluster.space.measure.Centroid;
//import ao.ai.cluster.space.measure.vector.VectorEuclidean;
//import ao.ai.cluster.trial.Clustering;
//import ao.ai.cluster.trial.ClusteringTrial;
//import ao.ai.cluster.trial.ParallelTrial;
import ao.holdem.abs.bucket.abstraction.access.tree.BucketTree;
import ao.holdem.abs.bucket.abstraction.access.tree.LongByteList;
import ao.holdem.abs.bucket.abstraction.access.tree.list.FullLongByteList;
import ao.holdem.abs.bucket.abstraction.access.tree.list.HalfLongByteList;
import ao.holdem.abs.bucket.abstraction.bucketize.def.Bucketizer;
import ao.holdem.canon.Canons;
import ao.holdem.canon.flop.Flop;
import ao.holdem.canon.hole.CanonHole;
import ao.holdem.canon.hole.HoleLookup;
import ao.holdem.canon.river.River;
import ao.holdem.canon.turn.Turn;
import ao.holdem.abs.bucket.index.detail.DetailLookup;
import ao.holdem.abs.bucket.index.detail.range.CanonRange;
import ao.holdem.abs.bucket.index.detail.range.RangeLookup;
import ao.holdem.model.Round;
import ao.util.data.AutovivifiedMap;
import ao.util.data.primitive.DoubleList;
import ao.util.misc.Equalizers;
import ao.util.misc.Factories;
import ao.util.misc.Factory;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: alex
 * Date: 5-Jul-2009
 * Time: 5:15:33 PM
 */
public class PotentialBucketizer
//        implements Bucketizer
{
//    //--------------------------------------------------------------------
//    private static final Logger LOG =
//            Logger.getLogger(PotentialBucketizer.class);
//
//    public static void main(String[] args) {
//        LongByteList holeBuckets =
//                new FullLongByteList(null, CanonHole.CANONS);
//
//        new PotentialBucketizer().bucketizeAll(
//                holeBuckets,
//                (byte) 20,
//                (byte) 4,
//                (byte) 8,  // 6
//                (byte) 14); // 10
//
//        Map<Byte, List<CanonHole>> byBucket =
//                new AutovivifiedMap<Byte, List<CanonHole>>(
//                        new Factory<List<CanonHole>>() {
//                            public List<CanonHole> newInstance() {
//                                return new ArrayList<CanonHole>();
//                            }});
//        for (int i = 0, j = CanonHole.CANONS - 1;
//                i < CanonHole.CANONS; i++, j--)
//        {
//            byBucket.get(
//                    holeBuckets.get(i)
//            ).add( HoleLookup.lookup(i) );
//        }
//
//        for (List<CanonHole> bucket : byBucket.values())
//        {
//            LOG.info(bucket);
//        }
//    }
//
//
//    //--------------------------------------------------------------------
//    public PotentialBucketizer()
//    {
//
//    }
//
//
//    //--------------------------------------------------------------------
//
//
//    //--------------------------------------------------------------------
//    public double bucketize(BucketTree.Branch branch, int numBuckets) {
////        if (branch.round() == Round.PREFLOP) {
////            return bucketizeAll(branch, numBuckets);
////        }
//
//        return 0;
//    }
//
//
//    //--------------------------------------------------------------------
//    private double bucketizeAll(
//            LongByteList preflopBranch,
//            byte       nHoleBuckets,
//            byte       nFlopBuckets,
//            byte       nTurnBuckets,
//            byte       nRiverBuckets)
//    {
//        LongByteList turnBuckets = turnBuckets(nTurnBuckets, nRiverBuckets);
//
//        CentroidDomain<Centroid<double[]>, double[]>
//                flopDomain = byNextRound(
//                    Round.FLOP, turnBuckets, nTurnBuckets);
//        LongByteList flopBuckets = new FullLongByteList(
//                null, Flop.CANONS);
//
////        for (byte nBuckets = 1; nBuckets < 16; nBuckets++)
////        {
////            double err = bucketizeAllPreRiver(
////                    Round.FLOP, flopDomain, flopBuckets, turnBuckets,
////                    nBuckets /*nFlopBuckets*/, nTurnBuckets);
////            System.out.println(nBuckets + "\t" + err);
////        }
//        bucketizeAllPreRiver(
//                Round.FLOP, flopDomain,
//                flopBuckets, turnBuckets,
//                nFlopBuckets, nTurnBuckets);
//
//        CentroidDomain<Centroid<double[]>, double[]>
//                holeDomain = byNextRound(
//                    Round.PREFLOP, flopBuckets, nFlopBuckets);
////        for (byte nBuckets = 1; nBuckets < 30; nBuckets++)
////        {
////            double err = bucketizeAllPreRiver(
////                Round.PREFLOP, holeDomain,
////                preflopBranch, flopBuckets,
////                nBuckets, nFlopBuckets);
////            System.out.println(nBuckets + "\t" + err);
////        }
//
//        return bucketizeAllPreRiver(
//                Round.PREFLOP, holeDomain,
//                preflopBranch, flopBuckets,
//                nHoleBuckets, nFlopBuckets);
//    }
//
//    private LongByteList turnBuckets(
//            byte nTurnBuckets, byte nRiverBuckets) {
//
//        LongByteList riverBuckets =
//                new HalfLongByteList(null, River.CANONS);
//
//        CentroidDomain<Centroid<double[]>, double[]>
//                turnDomain = turnDomain(nRiverBuckets, riverBuckets);
//
////        PersistentObjects.persist(turnDomain,
////                "/home/alex/proj/datamine/input/turnDomain.obj");
////        CentroidDomain<Centroid<double[]>, double[]>
////                turnDomain = PersistentObjects.retrieve(
////                    "/home/alex/proj/datamine/input/turnDomain.obj");
//
//        LongByteList turnBuckets = new HalfLongByteList(
//                null, Turn.CANONS);
//        bucketizeAllPreRiver(
//                Round.TURN, turnDomain,
//                turnBuckets, riverBuckets, nTurnBuckets, nRiverBuckets);
//        return turnBuckets;
//    }
//
//    private CentroidDomain<Centroid<double[]>, double[]> turnDomain(
//            byte nRiverBuckets, LongByteList riverBuckets) {
////        LongByteList riverBuckets =
////                new HalfLongByteList(null, RiverLookup.CANONS);
//        RiverBucketizer.bucketizeAll(nRiverBuckets, riverBuckets);
//        return byNextRound(
//                    Round.TURN, riverBuckets, nRiverBuckets);
//    }
//
//
//    //--------------------------------------------------------------------
//    private double bucketizeAllPreRiver(
//            Round      round,
//            CentroidDomain<Centroid<double[]>, double[]>
//                       byNextRound,
//            LongByteList roundBuckets,
//            LongByteList nextRoundBuckets,
//            byte       nRoundBuckets,
//            byte       nNextRoundBuckets)
//    {
//        LOG.debug("bucketizeAllPreRiver " + round);
//        ClusteringTrial<Centroid<double[]>> analyzer =
//                new ParallelTrial<Centroid<double[]>>(
//                        new KMeans<Centroid<double[]>>(),
//                        new TwoPassWcss<Centroid<double[]>>(),
//                        512 /*512*/);
//        Clustering clusters = analyzer.cluster(
//                byNextRound, nRoundBuckets);
//        analyzer.close();
//
//        LOG.debug("applying buckets");
////        Progress prog = new Progress(Canons.count(round));
//
//        @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
//        AutovivifiedMap<DoubleList, Integer> histIndex =
//                new AutovivifiedMap<DoubleList, Integer>(
//                        new Factory<Integer>() {
//                            private int next = 0;
//                            public Integer newInstance() {
//                                return next++;
//                            }
//                        });
//
//        // order of for loop (high -> low) must be consistent
//        for (int canon  = (int)(Canons.count(round) - 1);
//                 canon >= 0;
//                 canon--) {
//            DoubleList nextRoundHist = nextRoundHist(round, canon,
//                    nextRoundBuckets, nNextRoundBuckets);
//            roundBuckets.set(
//                    canon,
//                    clusters.cluster(
//                            histIndex.get(nextRoundHist)));
//
////            prog.checkpoint();
//        }
//
//        LOG.debug("done");
//        return clusters.error();
//    }
//
//
//    //--------------------------------------------------------------------
//    private CentroidDomain<Centroid<double[]>, double[]>
//            byNextRound(
//                    Round      round,
//                    LongByteList nextRoundBuckets,
//                    byte       nNextRoundBuckets)
//    {
//        LOG.debug("building " + round + " domain");
//
//        @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
//        AutovivifiedMap<DoubleList, int[]> histCount =
//                new AutovivifiedMap<DoubleList,int[]>(
//                        Factories.newArrayClone(new int[1]));
//
//        // order of for loop (high -> low) must be consistent
////        Progress progress = new Progress(Canons.count( round ));
//        for (int canon = (int)(Canons.count( round ) - 1);
//                 canon >= 0;
//                 canon--) {
//            DoubleList nextRoundHist =
//                    nextRoundHist(round,
//                            canon, nextRoundBuckets, nNextRoundBuckets);
//            histCount.get( nextRoundHist )[ 0 ]++;
////            progress.checkpoint();
//        }
//
//        LOG.debug("applying domain");
//        CentroidDomain<Centroid<double[]>, double[]> byNextRound =
//                new CentroidDomain<Centroid<double[]>, double[]>(
////                        VectorInfo.newFactory(nNextRoundBuckets)
//                        VectorEuclidean.newFactory(nNextRoundBuckets)
////                        Mahalanobis.newFactory(nRiverBuckets)
//                        , Equalizers.doubleArray()
//                );
//        for (Map.Entry<DoubleList, int[]> histogram :
//                histCount.entrySet()) {
//            byNextRound.add(
//                    histogram.getKey().toDoubleArray(),
//                    histogram.getValue()[ 0 ]);
//        }
//        //byNextRound.normalize();
//
//        LOG.debug("done\t" + histCount.size());
//        return byNextRound;
//    }
//
//
//    //--------------------------------------------------------------------
//    private DoubleList nextRoundHist(
//            Round      round,
//            int        forCanon,
//            LongByteList nextRoundBuckets,
//            byte       nNextRoundBuckets)
//    {
//        DoubleList histogram =
//                new DoubleList( nNextRoundBuckets, nNextRoundBuckets );
//
//        Round      nextRound      = round.next();
//        CanonRange nextRoundRange =
//                RangeLookup.lookupRange( round, forCanon );
//        for (long canon  = nextRoundRange.toInclusive();
//                  canon >= nextRoundRange.from();
//                  canon--)
//        {
//            histogram.increment(
//                    nextRoundBuckets.get(canon),
//                    DetailLookup.lookupRepresentation(
//                            nextRound, canon));
//        }
//        return histogram;
//    }
//
//
//    //--------------------------------------------------------------------
//    public void setThorough(boolean highPercision) {}
//
//
//    //--------------------------------------------------------------------
//    public String id() {
//        return "Potential";
//    }
}
