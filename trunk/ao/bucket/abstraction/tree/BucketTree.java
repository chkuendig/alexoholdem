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


    //--------------------------------------------------------------------
    public byte getHole(char canonHole);

    public byte getFlop(int canonFlop);


    //--------------------------------------------------------------------
//    public void add(byte  holeBucket,
//                    char canonHole);
//
//    public void add(byte holeBucket,
//                    byte flopBucket,
//                    int  canonFlop);
//
//    public void add(byte holeBucket,
//                    byte flopBucket,
//                    byte turnBucket,
//                    int  canonTurn);
//
//    public void add(byte holeBucket,
//                    byte flopBucket,
//                    byte turnBucket,
//                    byte riverBucket,
//                    long canonRiver);
//
//
//    //--------------------------------------------------------------------
//    public byte get(char canonHole);
//
//    public byte get(byte holeBucket,
//                    int  canonFlop);
//
//    public byte get(byte holeBucket,
//                    byte flopBucket,
//                    int  canonTurn);
//
//    public byte get(byte holeBucket,
//                    byte flopBucket,
//                    byte turnBucket,
//                    long canonRiver);


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
        
        public CanonDetail[][] subDetails();

        public void set(long canonIndex, byte bucket);
        public byte get(long canonIndex);

        public char             subBranchCount();
        public Iterable<Branch> subBranches();
    }
}
