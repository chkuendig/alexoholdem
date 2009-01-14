package ao.bucket.abstraction.tree;

import java.io.File;

/**
 * Date: Jan 8, 2009
 * Time: 10:51:29 AM
 */
public class BucketTreeImpl implements BucketTree
{
    //--------------------------------------------------------------------
    private static final File DIR = new File("lookup/");


    //--------------------------------------------------------------------
    

    private byte[] holes;
    private byte[] flops;



    //--------------------------------------------------------------------
    public BucketTreeImpl(String id)
    {

    }



    //--------------------------------------------------------------------
    public void add(byte  holeBucket,
                    short canonHole)
    {

    }

    public void add(byte holeBucket,
                    byte flopBucket,
                    int  canonFlop)
    {

    }

    public void add(byte holeBucket,
                    byte flopBucket,
                    byte turnBucket,
                    int  canonTurn)
    {

    }

    public void add(byte holeBucket,
                    byte flopBucket,
                    byte turnBucket,
                    byte riverBucket,
                    long canonRiver)
    {

    }


    //--------------------------------------------------------------------
    public byte get(char canonHole)
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
    public Branch root()
    {
        return null;
    }
}
