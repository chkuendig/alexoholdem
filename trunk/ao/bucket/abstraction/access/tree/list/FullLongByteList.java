package ao.bucket.abstraction.access.tree.list;

import ao.bucket.abstraction.access.tree.PersistentLongByteList;
import ao.util.io.Dir;
import ao.util.persist.PersistentBytes;

import java.io.File;

/**
 * Date: Jan 26, 2009
 * Time: 12:27:51 PM
 */
public class FullLongByteList implements PersistentLongByteList
{
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
            FILE_A = new File(Dir.get(dir),
                    StoredLongByteList.FILENAME_A);
            FILE_B = new File(        dir ,
                    StoredLongByteList.FILENAME_B);
        }
        else
        {   FILE_A = null;
            FILE_B = null;
        }


        LIST_A = retrieveOrCreate(FILE_A, (int)
                Math.min(size, Integer.MAX_VALUE));
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
    public byte maxBuckets() {
        return Byte.MAX_VALUE;
    }


    //--------------------------------------------------------------------
    public void set(long index, byte bucket)
    {
        if (index < Integer.MAX_VALUE)
        {
            LIST_A[ (int)  index                      ] = bucket;
        }
        else
        {
            LIST_B[ (int) (index - Integer.MAX_VALUE) ] = bucket;
        }

    }

    public byte get(long index)
    {
        if (index < Integer.MAX_VALUE)
        {
            return LIST_A[ (int)  index                      ];
        }
        else
        {
            return LIST_B[ (int) (index - Integer.MAX_VALUE) ];
        }
    }


    //--------------------------------------------------------------------
    public void flush()
    {
        PersistentBytes.persist(LIST_A, FILE_A);
        PersistentBytes.persist(LIST_B, FILE_B);
    }
}
