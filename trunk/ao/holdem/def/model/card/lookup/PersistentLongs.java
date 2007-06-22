package ao.holdem.def.model.card.lookup;

import java.io.*;

/**
 *
 */
public class PersistentLongs
{
    //--------------------------------------------------------------------
    private PersistentLongs() {}


    //--------------------------------------------------------------------
    public static long[] retrieve(String fromFile)
    {
        try
        {
            return doRead(fromFile);
        }
        catch (Exception e)
        {
            throw new Error( e );
        }
    }

    private static long[] doRead(String fileName) throws Exception
    {
        File cacheFile = new File(fileName);
        if (! cacheFile.canRead()) return null;

        long[] cached = new long[ (int)cacheFile.length()/8 ];

        DataInputStream cache =
                new DataInputStream(
                        new BufferedInputStream(
                                new FileInputStream(cacheFile),
                                1048576));
        for (int i = 0; i < cached.length; i++)
        {
            cached[ i ] = cache.readLong();
        }
        cache.close();

        return cached;
    }

    //--------------------------------------------------------------------
    public static void persist(
            long vals[], String fileName)
    {
        try
        {
            doPersist(vals, fileName);
        }
        catch (Exception e)
        {
            throw new Error( e );
        }
    }

    private static void doPersist(
            long vals[], String fileName) throws Exception
    {
        File cacheFile = new File(fileName);
        cacheFile.createNewFile();

        DataOutputStream cache =
                new DataOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(cacheFile)));
        for (long val : vals)
        {
            cache.writeLong( val );
        }
        cache.close();
    }
}
