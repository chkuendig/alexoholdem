package ao.bucket.abstraction.tree;

import ao.holdem.model.Round;

import java.util.List;

/**
 * Date: Jan 8, 2009
 * Time: 10:39:05 AM
 */
public interface BucketTree
{
    //--------------------------------------------------------------------
    public void add(byte  holeBucket,
                    short canonHole);

    public void add(byte holeBucket,
                    byte flopBucket,
                    int  canonFlop);

    public void add(byte holeBucket,
                    byte flopBucket,
                    byte turnBucket,
                    int  canonTurn);

    public void add(byte holeBucket,
                    byte flopBucket,
                    byte turnBucket,
                    byte riverBucket,
                    long canonRiver);


    //--------------------------------------------------------------------
    public byte get(char canonHole);

    public byte get(byte holeBucket,
                    int  canonFlop);

    public byte get(byte holeBucket,
                    byte flopBucket,
                    int  canonTurn);

    public byte get(byte holeBucket,
                    byte flopBucket,
                    byte turnBucket,
                    long canonRiver);


    //--------------------------------------------------------------------
    public void flush();


    //--------------------------------------------------------------------
    public Branch root();


    //--------------------------------------------------------------------
    public static interface Branch
    {
        public long firstParentCanon();
        public int  numParentCanon();
        
        public void add(byte bucket, long canonIndex);
        public byte get(long canonIndex);

        public List<Branch> subBranches();

        public Round round();

        public boolean containsHole (short canonHole);
        public boolean containsFlop (int   canonFlop);
        public boolean containsTurn (int   canonTurn);
        public boolean containsRiver(long  canonRiver);
    }
}
