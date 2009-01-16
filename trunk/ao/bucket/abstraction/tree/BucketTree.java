package ao.bucket.abstraction.tree;

import ao.bucket.index.detail.CanonDetail;
import ao.holdem.model.Round;

/**
 * Date: Jan 8, 2009
 * Time: 10:39:05 AM
 */
public interface BucketTree
{

    //--------------------------------------------------------------------
    public void setHole(char canonHole,
                        byte holeBucket);

    public void setFlop(int  canonFlop,
                        byte flopBucket);

    public void setTurn(int  canonTurn,
                        byte turnBucket);


    //--------------------------------------------------------------------
    public byte getHole(char canonHole);

    public byte getFlop(int canonFlop);

    public byte getTurn(int canonTurn);


    //--------------------------------------------------------------------
    public void flush();


    //--------------------------------------------------------------------
    public Branch root();


    //--------------------------------------------------------------------
    public static interface Branch
    {
        public Round round();
        public int[] parentCanons();
//        public byte  parentBucket();
        
        public CanonDetail[][] details();

        public void set(long canonIndex, byte bucket);
        public byte get(long canonIndex);

//        public char             subBranchCount();
        public Iterable<Branch> subBranches();

        public boolean isBucketized();

        public void flush();
    }
}
