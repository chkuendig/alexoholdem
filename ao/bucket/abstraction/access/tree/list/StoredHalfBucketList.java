package ao.bucket.abstraction.access.tree.list;

import ao.bucket.abstraction.access.tree.PersistentBucketList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * User: alex
 * Date: 13-Jun-2009
 * Time: 5:56:19 PM
 */
public class StoredHalfBucketList implements PersistentBucketList
{
    //--------------------------------------------------------------------
    private static final byte LO_MASK     =        0x0f;
    private static final byte HI_MASK     = (byte) 0xf0;
    private static final int  HI_INT_MASK =  0x000000f0;


    //--------------------------------------------------------------------
    private final RandomAccessFile IN;


    //--------------------------------------------------------------------
    public StoredHalfBucketList(File file)
    {
        try {
            IN = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
            throw new Error( e );
        }
    }


    //--------------------------------------------------------------------
    public byte maxBuckets() {
        return Byte.MAX_VALUE;
    }

    public void set(long index, byte bucket) {
        throw new UnsupportedOperationException();
    }

    public void flush() {}


    //--------------------------------------------------------------------
    public byte get(long index) {
        try {
            return doGet(index);
        } catch (IOException e) {
            throw new Error( e );
        }
    }
    private byte doGet(long index) throws IOException {
        IN.seek(HalfBucketList.halfIndex(index));
        byte pair = IN.readByte();
        return (byte)(HalfBucketList.isLow(index)
                      ?  pair & LO_MASK
                      : (pair & HI_INT_MASK) >>> 4);
    }
}
