package ao.bucket.abstraction.access.tree;

import ao.bucket.index.detail.CanonDetail;
import ao.holdem.model.Round;
import ao.util.misc.Traverser;

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

    public byte getRiver(long canonTurn);


    //--------------------------------------------------------------------
    public boolean isFlushed();
    public void    flush();


    //--------------------------------------------------------------------
    public Branch holes();

//    public BucketDecoder map();


    //--------------------------------------------------------------------
    public static interface Branch
    {
        public Round round();
        public int[] parentCanons();
        
        public CanonDetail[] details();
        public void          details(Traverser<CanonDetail> visit);

//        public void reset(byte fromBucket, byte toBucket);
        public void set(long canonIndex, byte bucket);
        public byte get(long canonIndex);

        public byte bucketCount();
        public Iterable<Branch> subBranches();
    }
}