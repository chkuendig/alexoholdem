package ao.bucket.abstraction.tree;

import java.util.Collection;

/**
 * Date: Jan 8, 2009
 * Time: 10:39:05 AM
 */
public interface BucketTree
{
    //--------------------------------------------------------------------
    public void set(byte  holeBucket,
                    short canonHole);

    public void set(byte holeBucket,
                    byte flopBucket,
                    int  canonFlop);

    public void set(byte holeBucket,
                    byte flopBucket,
                    byte turnBucket,
                    int  canonTurn);

    public void set(byte holeBucket,
                    byte flopBucket,
                    byte turnBucket,
                    byte riverBucket,
                    int  canonRiver);


    //--------------------------------------------------------------------
    public byte get(short canonHole);

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
    public Navigator navigate();


    //--------------------------------------------------------------------
    public static interface Navigator
    {
        public Collection<Navigator> branches();
    }
}
