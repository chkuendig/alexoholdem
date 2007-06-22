package ao.holdem.def.model.card.lookup;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PersistentShorts
{
    //--------------------------------------------------------------------
    private PersistentShorts() {}


    //--------------------------------------------------------------------
    public static short[] asArray(String fromFile)
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


    //--------------------------------------------------------------------
    public static List<Short> asList(String fileName)
    {
        short[] asArray = asArray(fileName);
        List<Short> asList = new ArrayList<Short>( asArray.length );
        for (short val : asArray)
        {
            asList.add( val );
        }
        return asList;
    }

    private static short[] doRead(String fileName) throws Exception
    {
        String cachedName = fileName + ".cache";

        short[] cached = doReadCached(cachedName);
        if (cached != null)
        {
            return cached;
        }
        else
        {
            List<Short> raw = doReadRaw(fileName);
            writeCache(raw, cachedName);

            cached = new short[ raw.size() ];
            for (int i = 0; i < cached.length; i++)
            {
                cached[ i ] = raw.get( i );
            }
            return cached;
        }
    }

    private static List<Short> doReadRaw(String fileName) throws Exception
    {
        File numberFile = new File( fileName );

        BufferedReader reader =
                new BufferedReader(new FileReader(numberFile));

        StringBuilder num  = new StringBuilder();
        List<Short>   nums = new ArrayList<Short>();

        String line;
        while ((line = reader.readLine()) != null)
        {
            if (line.length() == 0 || line.charAt(0) == '#') continue;

            for (int i = 0; i < line.length(); i++)
            {
                char charAtI = line.charAt( i );
                if (Character.isDigit(charAtI))
                {
                    num.append( charAtI );
                }
                else if (num.length() > 0)
                {
                    nums.add( Short.parseShort(num.toString()) );
                    num = new StringBuilder();
                }
            }
            if (num.length() > 0)
            {
                nums.add( Short.parseShort(num.toString()) );
                num = new StringBuilder();
            }
        }
        reader.close();

        return nums;
    }


    //--------------------------------------------------------------------
    private static void writeCache(
            List<Short> vals, String fileName) throws Exception
    {
        File cacheFile = new File(fileName);
        cacheFile.createNewFile();

        DataOutputStream cache =
                new DataOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(cacheFile)));
        for (Short val : vals)
        {
            cache.writeShort( val );
        }
        cache.close();
    }

    private static short[] doReadCached(String fileName) throws Exception
    {
        File cacheFile = new File(fileName);
        if (! cacheFile.canRead()) return null;

        short[] cached = new short[ (int)cacheFile.length()/2 ];

        DataInputStream cache =
                new DataInputStream(
                        new BufferedInputStream(
                                new FileInputStream(cacheFile)));
        for (int i = 0; i < cached.length; i++)
        {
            cached[ i ] = cache.readShort();
        }
        cache.close();

        return cached;
    }
}