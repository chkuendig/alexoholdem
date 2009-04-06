package ao.bucket.abstraction.bucketize;

import ao.ai.simple.starting_hands.PokerRoom;
import ao.bucket.abstraction.access.tree.BucketTree.Branch;
import ao.bucket.abstraction.alloc.BucketAllocator;
import ao.bucket.index.detail.CanonDetail;
import ao.bucket.index.hole.HoleLookup;
import ao.holdem.model.Round;
import ao.holdem.model.card.Hole;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Comparator;

/**
 * User: iscott
 * Date: Jan 9, 2009
 * Time: 10:24:57 AM
 */
public class BucketizerImpl implements Bucketizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(BucketizerImpl.class);


    //--------------------------------------------------------------------
    public boolean bucketize(Branch branch, byte nBuckets)
    {
        assert nBuckets > 0;
//        if (branch.isBucketized()) return false;

        if (branch.round() == Round.PREFLOP && nBuckets == 10) {
            return bucketizeTenHoles(branch);
        }


        CanonDetail[] details = branch.details();

        LOG.debug("bucketizing " + branch.round() + " branch of " +
                  details.length + " into " + nBuckets);

        Arrays.sort(details, new Comparator<CanonDetail>() {
            public int compare(CanonDetail a, CanonDetail b) {
                return Double.compare(
                        a.strength(),
                        b.strength());
            }
        });

        BucketAllocator alloc = new BucketAllocator(
                          details.length, (char) nBuckets);
        for (CanonDetail detail : details)
        {
            branch.set(detail.canonIndex(),
                       (byte) alloc.nextBucket(1));
        }
        return true;
    }


    //--------------------------------------------------------------------
    private boolean bucketizeTenHoles(Branch branch) {
        CanonDetail[] details = branch.details();

        LOG.debug("bucketizing " + branch.round() + " branch of " +
                  details.length + " into " + 10 + " using PokerRoom");

        for (CanonDetail detail : details)
        {
            Hole hole = HoleLookup.lookup((int)
                    detail.canonIndex()).reify();
            branch.set(detail.canonIndex(),
                       (byte)(PokerRoom.groupOf( hole ) - 1));
        }
        return true;
    }


    //--------------------------------------------------------------------
    public String id()
    {
        return "odds_homo";
    }

    @Override
    public String toString()
    {
        return id();
    }
}
