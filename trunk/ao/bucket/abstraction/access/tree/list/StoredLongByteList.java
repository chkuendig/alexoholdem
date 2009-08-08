package ao.bucket.abstraction.access.tree.list;

import ao.bucket.abstraction.access.tree.PersistentLongByteList;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * User: alex
 * Date: 13-Jun-2009
 * Time: 5:49:51 PM
 */
public class StoredLongByteList implements PersistentLongByteList
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(StoredLongByteList.class);

    public static final String FILENAME_A = "a.byte";
    public static final String FILENAME_B = "b.byte";



    //--------------------------------------------------------------------
    private final RandomAccessFile IN_A;
    private final RandomAccessFile IN_B;


    //--------------------------------------------------------------------
    public StoredLongByteList(File dir)
    {
        try {
//            if (! file.exists()) {
//                boolean created = file.createNewFile();
//                LOG.debug("Created: " + created);
//            }

            IN_A = new RandomAccessFile(new File(dir, FILENAME_A), "r");
            IN_B = new RandomAccessFile(new File(dir, FILENAME_B), "r");
        } catch (IOException e) {
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
        if (index < Integer.MAX_VALUE)
        {
            IN_A.seek(index);
            return IN_A.readByte();
        }
        else
        {
            IN_B.seek( (int) (index - Integer.MAX_VALUE) );
            return IN_B.readByte();
        }
    }
}
