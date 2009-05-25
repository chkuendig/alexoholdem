package ao.bucket.abstraction.access.tree.list;

import ao.bucket.abstraction.access.tree.BucketList;
import ao.util.io.Dir;
import ao.util.persist.PersistentBytes;

import java.io.File;

/**
 * Date: Jan 26, 2009
 * Time: 2:51:42 PM
 */
public class HalfBucketList implements BucketList
{
    //--------------------------------------------------------------------
    private static final File DIR = Dir.get("test");


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        int        size = 9;
        BucketList bl   = new HalfBucketList(
                                new File(DIR, "hb.test3.byte"), size);

        for (int i = 0; i < size; i++)
        {
//            if (bl.isEmpty(i)) {
                bl.set(i, (byte) (i % 15));
//                bl.flush(i, (char) 1);
//            }

            if (bl.get(i) != (i % 15)) {
                System.out.println("ERORR at " + i);
            }
        }
    }


    //--------------------------------------------------------------------
    private static final byte LO_MASK     =        0x0f;
    private static final byte HI_MASK     = (byte) 0xf0;
    private static final int  HI_INT_MASK =  0x000000f0;

//    private static final byte LO_SENTINAL = LO_MASK;
//    private static final byte HI_SENTINAL = HI_MASK;
//    private static final byte LH_SENTINAL = LO_SENTINAL | HI_SENTINAL;


    //--------------------------------------------------------------------
    private final File   FILE;
    private final byte[] LIST;


    //--------------------------------------------------------------------
    public HalfBucketList(File file, long size)
    {
        FILE = file;
        LIST = retrieveOrCreate( size );
    }

    private byte[] retrieveOrCreate(long size)
    {
        byte[] list = PersistentBytes.retrieve(FILE);
        if (list != null) return list;

        list = new byte[ halfIndex(size) +
                         (isLow(size) ? 0 : 1) ];
//        Arrays.fill(list, LH_SENTINAL);
        return list;
    }


    //--------------------------------------------------------------------
    public byte maxBuckets() {
        return LO_MASK + 1;
    }


    //--------------------------------------------------------------------
    public void set(long index, byte bucket)
    {
        assert bucket < LO_MASK;

        byte curr = LIST[ halfIndex(index) ];
        LIST[ halfIndex(index) ] =
                (byte)(isLow(index)
                       ? curr & HI_MASK | bucket
                       : curr & LO_MASK | bucket << 4);
    }

    public byte get(long index)
    {
        byte pair = LIST[ halfIndex(index) ];
        return (byte)(isLow(index)
                      ?  pair & LO_MASK
                      : (pair & HI_INT_MASK) >>> 4);
    }

//    public boolean isEmpty(long index)
//    {
//        return get(index) == LO_SENTINAL;
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
//        f.seek(halfIndex(fromCanon));
//        f.write(LIST,
//                halfIndex(fromCanon),
//                (isLow(fromCanon)
//                 ? halfIndex(canonCount + 1)
//                 : halfIndex(canonCount) + 1));
//        f.close();
//    }


    //--------------------------------------------------------------------
    private static int halfIndex(long index)
    {
        return (int)(index >> 1);
    }
    private static boolean isLow(long index)
    {
        return index % 2 == 0;
    }
}
