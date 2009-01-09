package ao.bucket.abstraction.tree;

/**
 * Date: Jan 8, 2009
 * Time: 10:51:29 AM
 */
public class BucketTreeImpl implements BucketTree
{
    //--------------------------------------------------------------------


    //--------------------------------------------------------------------



    //--------------------------------------------------------------------
    public BucketTreeImpl(String id)
    {

    }



    //--------------------------------------------------------------------
    public void set(byte  holeBucket,
                    short canonHole)
    {

    }

    public void set(byte holeBucket,
                    byte flopBucket,
                    int  canonFlop)
    {

    }

    public void set(byte holeBucket,
                    byte flopBucket,
                    byte turnBucket,
                    int  canonTurn)
    {

    }

    public void set(byte holeBucket,
                    byte flopBucket,
                    byte turnBucket,
                    byte riverBucket,
                    int  canonRiver)
    {

    }


    //--------------------------------------------------------------------
    public byte get(short canonHole)
    {
        return 0;
    }

    public byte get(byte holeBucket,
                    int  canonFlop)
    {
        return 0;
    }

    public byte get(byte holeBucket,
                    byte flopBucket,
                    int  canonTurn)
    {
        return 0;
    }

    public byte get(byte holeBucket,
                    byte flopBucket,
                    byte turnBucket,
                    long canonRiver)
    {
        return 0;
    }


    //--------------------------------------------------------------------
    public void flush()
    {

    }


    //--------------------------------------------------------------------
    public Navigator navigate()
    {
        return null;
    }
}
