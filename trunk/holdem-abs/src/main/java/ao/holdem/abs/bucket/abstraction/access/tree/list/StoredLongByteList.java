package ao.holdem.abs.bucket.abstraction.access.tree.list;

import ao.holdem.abs.bucket.abstraction.access.tree.PersistentLongByteList;
import ao.util.math.Calc;
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
    public int maxBuckets() {
        return FullLongByteList.MAX_BUCKETS;
    }

    public void set(long index, int bucket) {
        throw new UnsupportedOperationException();
    }

    public void flush() {}


    //--------------------------------------------------------------------
    public int get(long index) {
        try {
            return doGet(index);
        } catch (IOException e) {
            throw new Error( e );
        }
    }
    private int doGet(long index) throws IOException {
        byte signed;
        if (index < Integer.MAX_VALUE)
        {
            IN_A.seek(index);
            signed = IN_A.readByte();
        }
        else
        {
            IN_B.seek( (int) (index - Integer.MAX_VALUE) );
            signed = IN_B.readByte();
        }
        return Calc.unsigned( signed );
    }
}
