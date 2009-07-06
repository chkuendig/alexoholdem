package ao.bucket.abstraction.access.tree.list;

import ao.bucket.abstraction.access.tree.PersistentBucketList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * User: alex
 * Date: 13-Jun-2009
 * Time: 5:49:51 PM
 */
public class StoredBucketList implements PersistentBucketList
{
    //--------------------------------------------------------------------
    private final RandomAccessFile IN;


    //--------------------------------------------------------------------
    public StoredBucketList(File file)
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
        IN.seek(index);
        return IN.readByte();
    }
}
