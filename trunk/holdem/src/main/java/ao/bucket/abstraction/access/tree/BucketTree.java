package ao.bucket.abstraction.access.tree;

import ao.bucket.index.detail.CanonDetail;
import ao.holdem.model.Round;

import java.util.List;

/**
 * Date: Jan 8, 2009
 * Time: 10:39:05 AM
 */
public interface BucketTree
{
    //--------------------------------------------------------------------
    public int[] maxBucketBranch();


    //--------------------------------------------------------------------
    public void setHole(char canonHole,
                        int  holeBucket);

    public void setFlop(int  canonFlop,
                        int  flopBucket);

    public void setTurn(int  canonTurn,
                        int  turnBucket);


    //--------------------------------------------------------------------
    public int getHole (int  canonHole);

    public int getFlop (int  canonFlop);

    public int getTurn (int  canonTurn);

    public int getRiver(long canonRiver);


    //--------------------------------------------------------------------
    public boolean isFlushed();
    public void    flush();


    //--------------------------------------------------------------------
    public Branch holes();

//    public BucketDecoder map();


    //--------------------------------------------------------------------
    public static interface Branch extends LongByteList
    {
        public Round round();
        public int[] parentCanons();
        
        public CanonDetail[] details();
//        public void          details(Traverser<CanonDetail> visit);

//        public void set(long canonIndex, byte bucket);
//        public byte get(long canonIndex);

        public byte bucketCount();
        public List<Branch> subBranches();
    }
}
