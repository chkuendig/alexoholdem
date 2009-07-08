package ao.bucket.abstraction.bucketize.smart;

import ao.bucket.abstraction.access.tree.BucketList;
import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.access.tree.list.HalfBucketList;
import ao.bucket.abstraction.bucketize.def.Bucketizer;
import ao.bucket.index.canon.river.RiverLookup;
import ao.bucket.index.canon.turn.TurnLookup;
import ao.bucket.index.detail.CanonRange;
import ao.bucket.index.detail.turn.TurnDetails;
import ao.bucket.index.detail.turn.TurnRivers;
import ao.unsupervised.cluster.space.impl.CentroidDomain;
import ao.unsupervised.cluster.space.measure.Centroid;
import ao.unsupervised.cluster.space.measure.vector.VectorEuclidean;

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

        BucketList turnBuckets = new HalfBucketList(
                null, TurnLookup.CANONS);
        bucketizerAllTurns(
                turnBuckets, riverBuckets,
                nTurnBuckets, nRiverBuckets);




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
                    TurnDetails.lookup(canonTurn).represents());
        }
        

//        BucketTree.Branch allRivers = new

        return -1;
    }

    private double[] riverHist(
            int        forTurn,
            BucketList riverBuckets,
            byte       nRiverBuckets)
    {
        double histogram[] = new double[ nRiverBuckets ];

        CanonRange rivers = TurnRivers.rangeOf( forTurn );
        for (int river  = (int) rivers.fromCanonIndex();
                 river <= rivers.upToAndIncluding();
                 river++) {
            histogram[
                riverBuckets.get(river)]++;
        }

        return histogram;
    }


    //--------------------------------------------------------------------
    public String id() {
        return "Potential";
    }
}
