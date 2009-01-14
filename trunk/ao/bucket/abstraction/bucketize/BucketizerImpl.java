package ao.bucket.abstraction.bucketize;

import ao.bucket.abstraction.alloc.BucketAllocator;
import ao.bucket.abstraction.tree.BucketTree.Branch;
import ao.bucket.index.detail.CanonDetail;
import ao.bucket.index.detail.CanonDetails;
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
    public void bucketize(Branch branch, byte nBuckets)
    {
        assert nBuckets > 0;
        LOG.debug("bucketizing " + branch.round() + " branch of " +
                  branch.numParentCanon() + " into " + nBuckets);

        BucketAllocator alloc = new BucketAllocator(
                          branch.numParentCanon(), (char) nBuckets);
        for (CanonDetail holeCanon : sortCanonDetail(branch))
        {
            branch.add((byte) alloc.nextBucket(1),
                       holeCanon.canonIndex());
        }
    }


    //--------------------------------------------------------------------
    private CanonDetail[] sortCanonDetail(Branch branch)
    {
        CanonDetail[] inOrder =
                CanonDetails.lookup(
                    branch.round(),
                    branch.firstParentCanon(),
                    branch.numParentCanon());

        Arrays.sort(inOrder, new Comparator<CanonDetail>() {
            public int compare(CanonDetail a, CanonDetail b) {
                return Double.compare(
                        a.strengthVsRandom(),
                        b.strengthVsRandom());
            }
        });

        return inOrder;
    }


    //--------------------------------------------------------------------
    public String id()
    {
        return "odds_equal";
    }

    @Override
    public String toString()
    {
        return id();
    }
}
