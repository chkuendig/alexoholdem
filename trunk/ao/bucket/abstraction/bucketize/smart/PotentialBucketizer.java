package ao.bucket.abstraction.bucketize.smart;

import ao.bucket.abstraction.access.tree.BucketList;
import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.access.tree.list.HalfBucketList;
import ao.bucket.abstraction.bucketize.def.Bucketizer;
import ao.bucket.index.canon.river.RiverLookup;
import ao.bucket.index.canon.turn.TurnLookup;
import ao.bucket.index.detail.CanonRange;
import ao.bucket.index.detail.river.RiverEvalLookup;
import ao.bucket.index.detail.turn.TurnRivers;
import ao.unsupervised.cluster.analysis.KMeans;
import ao.unsupervised.cluster.error.TwoPassWcss;
import ao.unsupervised.cluster.space.impl.CentroidDomain;
import ao.unsupervised.cluster.space.measure.Centroid;
import ao.unsupervised.cluster.space.measure.vector.VectorEuclidean;
import ao.unsupervised.cluster.trial.Clustering;
import ao.unsupervised.cluster.trial.ClusteringTrial;
import ao.unsupervised.cluster.trial.ParallelTrial;

/**
 * User: alex
 * Date: 5-Jul-2009
 * Time: 5:15:33 PM
 */
public class PotentialBucketizer implements Bucketizer
{
    //--------------------------------------------------------------------
    public static void main(String[] args) {
        new PotentialBucketizer().bucketizeAll(
                null,
                (byte) 16,
                (byte) 10,
                (byte) 5,
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
        BucketList riverBuckets = new HalfBucketList(
                null, RiverLookup.CANONS);
        RiverBucketizer.bucketizeAll(
                        nRiverBuckets, riverBuckets);

        for (byte t = 1; t < 30; t++)
        {
            BucketList turnBuckets = new HalfBucketList(
                    null, TurnLookup.CANONS);
            double err = bucketizerAllTurns(
                    turnBuckets, riverBuckets,
                    t /*nTurnBuckets*/, nRiverBuckets);
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
            BucketList turnBuckets,
            BucketList riverBuckets,
            byte       nTurnBuckets,
            byte       nRiverBuckets)
    {
        CentroidDomain<Centroid<double[]>, double[]> byRiver =
                new CentroidDomain<Centroid<double[]>, double[]>(
                        VectorEuclidean.newFactory(nRiverBuckets));

        for (int canonTurn = 0;
                 canonTurn < TurnLookup.CANONS;
                 canonTurn++) {
            byRiver.add(
                    riverHist(canonTurn, riverBuckets, nRiverBuckets),
                    1);
        }

        ClusteringTrial<Centroid<double[]>> analyzer =
                new ParallelTrial<Centroid<double[]>>(
                        new KMeans<Centroid<double[]>>(),
                        new TwoPassWcss<Centroid<double[]>>(),
                        512);
        Clustering clusters = analyzer.cluster(
                                         byRiver, nTurnBuckets);
        analyzer.close();

        for (int canonTurn = 0;
                 canonTurn < TurnLookup.CANONS;
                 canonTurn++) {
            turnBuckets.set(
                    canonTurn,
                    clusters.cluster(canonTurn));
        }

        return clusters.error();
    }

    private double[] riverHist(
            final int        forTurn,
            final BucketList riverBuckets,
            final byte       nRiverBuckets)
    {
        final double histogram[] = new double[ nRiverBuckets ];
        RiverEvalLookup.traverse(
                new CanonRange[]{TurnRivers.rangeOf( forTurn )},
                new RiverEvalLookup.VsRandomVisitor() {
                    public void traverse(
                            long   canonIndex,
                            double strengthVsRandom,
                            byte   represents) {
                        histogram[ riverBuckets.get(canonIndex) ] +=
                                                            represents;
                    }
                });
        return histogram;
    }


    //--------------------------------------------------------------------
    public String id() {
        return "Potential";
    }
}
