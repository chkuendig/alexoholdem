package ao.bucket.abstraction.access;

import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.hole.CanonHole;
import ao.bucket.index.river.River;
import ao.bucket.index.turn.Turn;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.regret.holdem.HoldemBucket;

/**
 * Date: Jan 19, 2009
 * Time: 12:37:00 PM
 */
public class BucketAgglom
{
    //--------------------------------------------------------------------
    private final BucketTree bucketTree;


    //--------------------------------------------------------------------
    public BucketAgglom(BucketTree tree)
    {
        bucketTree = tree;
    }



    //--------------------------------------------------------------------
    public HoldemBucket root()
    {
        return null;
    }


    //--------------------------------------------------------------------
    public HoldemBucket[] computeBuckets(
            Hole      hole,
            Community community)
    {
        CanonHole canonHole  = hole.asCanon();
        byte      holeBucket = bucketTree.getHole(canonHole.canonIndex());

        Flop  flop        = canonHole.addFlop(community);
        byte  flopBucket  = bucketTree.getFlop( flop.canonIndex() );

        Turn  turn        = flop.addTurn(community.turn());
        byte  turnBucket  = bucketTree.getTurn( turn.canonIndex() );

        River river       = turn.addRiver(community.river());
        byte  riverBucket = bucketTree.getRiver( river.canonIndex() );


        HoldemBucket hBucket =  root().nextBucket(  holeBucket );
        HoldemBucket fBucket = hBucket.nextBucket(  flopBucket );
        HoldemBucket tBucket = fBucket.nextBucket(  turnBucket );
        HoldemBucket rBucket = tBucket.nextBucket( riverBucket );
        
        return new HoldemBucket[] {
                hBucket, fBucket, tBucket, rBucket};
    }
}
