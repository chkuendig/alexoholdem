package ao.bucket.abstraction.tree;

import ao.bucket.index.flop.FlopLookup;
import ao.holdem.model.card.Hole;
import ao.util.io.Dir;
import ao.util.persist.PersistentBytes;

import java.io.File;

/**
 * Date: Jan 8, 2009
 * Time: 10:51:29 AM
 */
public class BucketTreeImpl implements BucketTree
{
    //--------------------------------------------------------------------
    private static final File DIR = Dir.get("lookup/bucket/tree/");
    


    //--------------------------------------------------------------------
    private final File persistDir;

    private final byte[] holes;
    private final byte[] flops;



    //--------------------------------------------------------------------
    public BucketTreeImpl(String id)
    {
        persistDir = new File(DIR, id);

        holes = retrieveOrCreate("holes",       Hole.CANONICAL_COUNT);
        flops = retrieveOrCreate("flops", FlopLookup.CANONICAL_COUNT);
    }

    private byte[] retrieveOrCreate(String filename, int canonCount)
    {
        File   fullName = new File(persistDir, filename);
        byte[] buckets  = PersistentBytes.retrieve(
                                fullName.toString() );
        if (buckets == null)
            buckets = new byte[ canonCount ];
        return buckets;
    }


    //--------------------------------------------------------------------
    public void add(byte holeBucket,
                    char canonHole)
    {
        holes[ canonHole ] = holeBucket;
    }

    public void add(byte holeBucket,
                    byte flopBucket,
                    int  canonFlop)
    {
        flops[ canonFlop ] = flopBucket;
    }

    public void add(byte holeBucket,
                    byte flopBucket,
                    byte turnBucket,
                    int  canonTurn)
    {
        throw new UnsupportedOperationException();
    }

    public void add(byte holeBucket,
                    byte flopBucket,
                    byte turnBucket,
                    byte riverBucket,
                    long canonRiver)
    {
        throw new UnsupportedOperationException();
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
