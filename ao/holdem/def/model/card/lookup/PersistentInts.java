package ao.holdem.def.model.card.lookup;

import java.io.*;

/**
 *
 */
public class PersistentInts
{
    //--------------------------------------------------------------------
    private PersistentInts() {}


    //--------------------------------------------------------------------
    public static int[] retrieve(String fromFile)
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

    private static int[] doRead(String fileName) throws Exception
    {
        File cacheFile = new File(fileName);
        if (! cacheFile.canRead()) return null;

        int[] cached = new int[ (int)cacheFile.length()/4 ];

        DataInputStream cache =
                new DataInputStream(
                        new BufferedInputStream(
                                new FileInputStream(cacheFile),
                                1048576));
        for (int i = 0; i < cached.length; i++)
        {
            cached[ i ] = cache.readInt();
        }
        cache.close();

        return cached;
    }

    //--------------------------------------------------------------------
    public static void persist(
            int vals[], String fileName)
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
            int vals[], String fileName) throws Exception
    {
        File cacheFile = new File(fileName);
        cacheFile.createNewFile();

        DataOutputStream cache =
                new DataOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(cacheFile)));
        for (int val : vals)
        {
            cache.writeInt( val );
        }
        cache.close();
    }
}
