package ao.bucket.abstraction.access.tree.list;

import ao.bucket.abstraction.access.tree.BucketList;
import ao.util.persist.PersistentBytes;

import java.io.File;

/**
 * Date: Jan 26, 2009
 * Time: 12:27:51 PM
 */
public class BucketListImpl implements BucketList
{
    //--------------------------------------------------------------------
//    private static final byte SENTINAL = -1;


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
        byte[] list = PersistentBytes.retrieve(FILE);
        if (list != null) return list;

        list = new byte[ size ];
//        Arrays.fill(list, SENTINAL);
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

//    public boolean isEmpty(long index)
//    {
//        return get(index) == SENTINAL;
//    }


    //--------------------------------------------------------------------
    public void flush()
    {
        PersistentBytes.persist(LIST, FILE);
    }

//    public void flush(long fromCanon, char canonCount)
//    {
//        try
//        {
//            doFlush(fromCanon, canonCount);
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//    }
//    private void doFlush(long fromCanon, char canonCount)
//            throws IOException
//    {
//        if (! FILE.canRead()) {
//            flush();
//            return;
//        }
//
//        RandomAccessFile f = new RandomAccessFile(FILE, "rw");
//        f.seek(fromCanon);
//        f.write(LIST, (int) fromCanon, canonCount);
//        f.close();
//    }
}
