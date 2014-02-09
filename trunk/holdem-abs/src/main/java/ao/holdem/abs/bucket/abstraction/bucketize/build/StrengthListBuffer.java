package ao.holdem.abs.bucket.abstraction.bucketize.build;

import ao.holdem.abs.bucket.abstraction.access.tree.BucketTree;
import ao.holdem.abs.bucket.abstraction.bucketize.linear.IndexedStrengthList;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * User: alex
 * Date: 6-Jun-2009
 * Time: 2:51:38 PM
 */
public class StrengthListBuffer
{
    //--------------------------------------------------------------------
    private static final int BUFFER_SIZE = 5;


    //--------------------------------------------------------------------
    private final BlockingQueue<IndexedStrengthList>
            strengthLists =
                new ArrayBlockingQueue<IndexedStrengthList>(BUFFER_SIZE);

//    private final CountDownLatch branchConsumed = new CountDownLatch(1);
//    private final CountDownLatch branchReady    = new CountDownLatch(1);


    //--------------------------------------------------------------------
    public StrengthListBuffer(final List<BucketTree.Branch> branches)
    {
        new Thread(new Runnable() {
            public void run() {
                trailBranches(branches);
            }
        }).start();
    }

    //--------------------------------------------------------------------
    private void trailBranches(List<BucketTree.Branch> branches)
    {
        for (int i = 0; i < branches.size();)
        {
            try {
                strengthLists.offer(
                    IndexedStrengthList.strengths(
                            branches.get(i++)),
                    1000, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                throw new Error( e );
            }
        }
    }


    //--------------------------------------------------------------------
    public IndexedStrengthList nextBranchStrengths()
    {
        try {
            return strengthLists.poll(1000, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            throw new Error( e );
        }
    }
}
