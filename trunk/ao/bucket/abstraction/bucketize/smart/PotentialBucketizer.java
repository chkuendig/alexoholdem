package ao.bucket.abstraction.bucketize.smart;

import ao.bucket.abstraction.access.tree.BucketList;
import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.access.tree.list.HalfBucketList;
import ao.bucket.abstraction.bucketize.def.Bucketizer;
import ao.bucket.index.canon.river.RiverLookup;
import ao.bucket.index.canon.turn.TurnLookup;
import ao.holdem.model.Round;

/**
 * User: alex
 * Date: 5-Jul-2009
 * Time: 5:15:33 PM
 */
public class PotentialBucketizer implements Bucketizer
{
    //--------------------------------------------------------------------
    public PotentialBucketizer()
    {

    }


    //--------------------------------------------------------------------


    //--------------------------------------------------------------------
    public double bucketize(BucketTree.Branch branch, byte numBuckets) {
        if (branch.round() == Round.PREFLOP) {
//            return bucketizeAll(branch, numBuckets);
        }

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
                turnBuckets, riverBuckets, nTurnBuckets);




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
            byte       nTurnBuckets)
    {
//        Domain


//        BucketTree.Branch allRivers = new

        return -1;
    }


    //--------------------------------------------------------------------
    public String id() {
        return "Potential";
    }
}
