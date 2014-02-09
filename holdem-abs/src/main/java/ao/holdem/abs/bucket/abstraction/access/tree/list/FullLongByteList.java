package ao.holdem.abs.bucket.abstraction.access.tree.list;

import ao.holdem.abs.bucket.abstraction.access.tree.PersistentLongByteList;
import ao.util.io.Dirs;
import ao.util.math.Calc;
import ao.util.persist.PersistentBytes;

import java.io.File;

/**
 * Date: Jan 26, 2009
 * Time: 12:27:51 PM
 */
public class FullLongByteList implements PersistentLongByteList
{
    //--------------------------------------------------------------------
    public static final int MAX_BUCKETS = (1 << Byte.SIZE);


    public static void main(String[] args) {
        FullLongByteList t = new FullLongByteList(null, 300);

        for (int i = 0; i < 300; i++) {
            t.set(i, i);
            System.out.println(i + "\t" + t.get(i));
        }
    }


    //--------------------------------------------------------------------
    private final File   FILE_A;
    private final File   FILE_B;

    private final byte[] LIST_A;
    private final byte[] LIST_B;


    //--------------------------------------------------------------------
    public FullLongByteList(File dir, long size)
    {
        if (dir != null)
        {
            FILE_A = new File(Dirs.get(dir),
                    StoredLongByteList.FILENAME_A);
            FILE_B = new File(        dir ,
                    StoredLongByteList.FILENAME_B);
        }
        else
        {   FILE_A = null;
            FILE_B = null;
        }


        LIST_A = retrieveOrCreate(FILE_A, (int)
                Math.min(size,  Integer.MAX_VALUE));
        LIST_B = retrieveOrCreate(FILE_B, (int)
                Math.max(size - Integer.MAX_VALUE, 0));
    }

    private byte[] retrieveOrCreate(File file, int size)
    {
        byte       list[];
        if (       file                                   != null &&
                  (list = PersistentBytes.retrieve(file)) != null) {
            return list;
        }
        return new byte[ size ];
    }


    //--------------------------------------------------------------------
    public int maxBuckets() {
        return MAX_BUCKETS;
    }


    //--------------------------------------------------------------------
    public void set(long index, int bucket)
    {
        if (index < Integer.MAX_VALUE)
        {
            LIST_A[ (int)  index                      ] = (byte) bucket;
        }
        else
        {
            LIST_B[ (int) (index - Integer.MAX_VALUE) ] = (byte) bucket;
        }

        // if (index > Integer.MAX_VALUE) with Calc.signedPart()
        //  would make more sense here, but reasons of not wanting
        //  to recalculate my data and being too lazy to transform the
        //  data in a more intelligent way,
        //  i'm gonna leave it as is for now.
    }

    public int get(long index)
    {
        return Calc.unsigned(
                (index < Integer.MAX_VALUE)
                ? LIST_A[ (int)  index                      ]
                : LIST_B[ (int) (index - Integer.MAX_VALUE) ]);
    }


    //--------------------------------------------------------------------
    public void flush()
    {
        PersistentBytes.persist(LIST_A, FILE_A);
        PersistentBytes.persist(LIST_B, FILE_B);
    }
}
