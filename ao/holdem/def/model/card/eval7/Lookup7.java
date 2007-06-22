package ao.holdem.def.model.card.eval7;


import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.cards.Hand;
import ao.util.stats.Combiner;

import java.io.*;

/**
 *
 */
public class Lookup7
{
    //--------------------------------------------------------------------
    private Lookup7() {}


    //--------------------------------------------------------------------
    // brocken up into 128 pices of 1,048,576 values
//    private static final AtomicReference<short[][]> values =
//            new AtomicReference<short[][]>();
    private static final short values[][] = alloc();


//    private static short[][] readOrPopulate()
    static
    {
        new Thread(new Runnable() {
            public void run() {
                if (! read(values)) populate(values);
            }
        }).start();
    }

    //--------------------------------------------------------------------
    private static boolean read(short vals[][])
    {
        try
        {
            return doRead(vals);
        }
        catch (Exception e)
        {
            throw new Error( e );
        }
    }

    private static boolean doRead(short vals[][]) throws Exception
    {
        File cacheFile = new File("lookup/cache7.bin");
        if (! cacheFile.canRead()) return false;

        DataInputStream cache =
                new DataInputStream(
                        new BufferedInputStream(
                                new FileInputStream(cacheFile)));
        for (int i = 0; i < 133784560; i++)
        {
            set(vals, i, cache.readShort());
        }
        cache.close();
        return true;
    }


    //--------------------------------------------------------------------
    private static void populate(short vals[][])
    {
        try
        {
            doPopulate(vals);
        }
        catch (Exception e)
        {
            throw new Error( e );
        }
    }

    private static void doPopulate(short vals[][]) throws Exception
    {
        compute(vals);

        File cacheFile = new File("lookup/cache7.bin");
        cacheFile.createNewFile();

        DataOutputStream cache =
                new DataOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(cacheFile)));
        for (int i = 0; i < 133784560; i++)
        {
            cache.writeShort( get(vals, i) );
        }
        cache.close();
    }


    //--------------------------------------------------------------------
    private static void compute(short vals[][])
    {
        Combiner<Card> combiner = new Combiner<Card>(Card.values(), 7);
        while (combiner.hasMoreElements())
        {
            Card handOf7[] = combiner.nextElement();
            int index = UniqueIndex.index52c7(mask(handOf7));
            if (isNull(vals, index))
            {
                set(vals, index, new Hand( handOf7 ).value());
            }
        }
    }


    //--------------------------------------------------------------------
    private static short[][] alloc()
    {
        // 133 784 560
        short table[][] = new short[128][];
        for (int i = 0; i < 127; i++)
        {
            table[i] = new short[ 1048576 ];
        }
        table[127] = new short[ 615408 ];
        return table;
    }

    private static boolean isNull(short[][] table, int index)
    {
        // will never return true for an entry that had its value set.
        return table[ index >> 20 ][ index & 0xfffff ] == 0;
    }

    private static short set(short[][] table, int index, short val)
    {
        table[ index >> 20 ][ index & 0xfffff ] = val;
        return val;
    }

    private static short get(short[][] table, int index)
    {
        return table[ index >> 20 ][ index & 0xfffff ];
    }


    //--------------------------------------------------------------------
    public static short handValue(Card... sevenCards)
    {
        assert sevenCards.length == 7;

        int index = UniqueIndex.index52c7( mask(sevenCards) );
        short val = get(values, index);
        return val == 0
                ? set(values, index, new Hand( sevenCards ).value())
                : val;
    }

    public static short handValue(
            Card c1, Card c2, Card c3, Card c4,
            Card c5, Card c6, Card c7)
    {
        int index = UniqueIndex.index52c7(
                        mask(c1, c2, c3, c4, c5, c6, c7) );
        short val = get(values, index);
        return val == 0
                ? set(values, index,
                      new Hand(c1, c2, c3, c4, c5, c6, c7).value())
                : val;
    }


    //--------------------------------------------------------------------
    public static long mask(
            Card c1, Card c2, Card c3, Card c4,
            Card c5, Card c6, Card c7)
    {
        return 1L << c1.ordinal() |
               1L << c2.ordinal() |
               1L << c3.ordinal() |
               1L << c4.ordinal() |
               1L << c5.ordinal() |
               1L << c6.ordinal() |
               1L << c7.ordinal();
    }

    private static long mask(Card... sevenCards)
    {
        long mask = 0;
        for (Card card : sevenCards)
        {
            mask |= (1L << card.ordinal());
        }
        return mask;
    }
}
