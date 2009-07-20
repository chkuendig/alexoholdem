package ao.bucket.abstraction.access.tree.list;

import ao.bucket.abstraction.access.tree.PersistentBucketList;
import ao.util.persist.PersistentBytes;

import java.io.File;

/**
 * Date: Jan 26, 2009
 * Time: 12:27:51 PM
 */
public class BucketListImpl implements PersistentBucketList
{
    //--------------------------------------------------------------------
    private final File   FILE;
    private final byte[] LIST;


    //--------------------------------------------------------------------
    public BucketListImpl(File file, int size)
    {
        FILE = file;
        LIST = retrieveOrCreate( size );
    }

    private byte[] retrieveOrCreate(int size)
    {
        byte[] list =
                FILE == null
                ? null
                : PersistentBytes.retrieve(FILE);
        if (list != null) return list;

        list = new byte[ size ];
        return list;
    }


    //--------------------------------------------------------------------
    public byte maxBuckets() {
        return Byte.MAX_VALUE;
    }


    //--------------------------------------------------------------------
    public void set(long index, byte bucket)
    {
        LIST[ (int) index ] = bucket;
    }

    public byte get(long index)
    {
        return LIST[ (int) index ];
    }


    //--------------------------------------------------------------------
    public void flush()
    {
        PersistentBytes.persist(LIST, FILE);
    }
}
