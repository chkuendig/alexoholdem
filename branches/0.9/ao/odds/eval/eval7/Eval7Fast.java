package ao.odds.eval.eval7;


import ao.holdem.model.card.Card;
import ao.odds.eval.eval_567.EvalSlow;
import ao.util.math.stats.Combiner;
import ao.util.math.stats.Combo;

import java.io.*;
import java.util.Arrays;

/**
 *
 */
public class Eval7Fast
{
    //--------------------------------------------------------------------
    private Eval7Fast() {}


    //--------------------------------------------------------------------
    private static final int NUM_HANDS = (int) Combo.choose(52, 7);

//    private static final AtomicReference<short[][]> values =
//            new AtomicReference<short[][]>();
    private static final short values[] = alloc();


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
    private static boolean read(short vals[])
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

    private static boolean doRead(short vals[]) throws Exception
    {
        File cacheFile = new File(Eval7FastLookup.F_CACHE);
        if (! cacheFile.canRead()) return false;

        DataInputStream cache =
                new DataInputStream(
                        new BufferedInputStream(
                                new FileInputStream(cacheFile),
                                1048576));
        for (int i = 0; i < NUM_HANDS; i++)
        {
            set(vals, i, cache.readShort());
        }
        cache.close();
        return true;
    }


    //--------------------------------------------------------------------
    private static void populate(short vals[])
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

    private static void doPopulate(short vals[]) throws Exception
    {
        compute(vals);

        File cacheFile = new File(Eval7FastLookup.F_CACHE);
        
        //noinspection ResultOfMethodCallIgnored
        cacheFile.createNewFile();

        DataOutputStream cache =
                new DataOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(cacheFile)));
        for (int i = 0; i < NUM_HANDS; i++)
        {
            cache.writeShort( get(vals, i) );
        }
        cache.close();
    }


    //--------------------------------------------------------------------
    private static void compute(short vals[])
    {
        Combiner<Card> combiner = new Combiner<Card>(Card.values(), 7);
        while (combiner.hasMoreElements())
        {
            Card handOf7[] = combiner.nextElement();
            int index = Eval7FastLookup.index52c7(mask(handOf7));
            if (isNull(vals, index))
            {
                set(vals, index, EvalSlow.valueOf( handOf7 ));
            }
        }
    }


    //--------------------------------------------------------------------
    private static short[] alloc()
    {
        short table[] = new short[ NUM_HANDS ];
        Arrays.fill(table, (short) -1);
        return table;
    }

    private static boolean isNull(short[] table, int index)
    {
        // will never return true for an entry that had its value set.
        return table[ index ] == -1;
    }

    private static short set(short[] table, int index, short val)
    {
        table[ index ] = val;
        return val;
    }

    private static short get(short[] table, int index)
    {
        return table[ index ];
    }


    //--------------------------------------------------------------------
    public static short valueOf(Card... sevenCards)
    {
        assert sevenCards.length == 7;

        int index = Eval7FastLookup.index52c7( mask(sevenCards) );
        short val = get(values, index);
        return val == -1
                ? set(values, index, EvalSlow.valueOf( sevenCards ))
                : val;
    }

    public static short valueOf(
            Card c1, Card c2, Card c3, Card c4,
            Card c5, Card c6, Card c7)
    {
        int index = Eval7FastLookup.index52c7(
                        mask(c1, c2, c3, c4, c5, c6, c7) );
        short val = get(values, index);
        return val == -1
                ? set(values, index,
                      EvalSlow.valueOf(c1, c2, c3, c4, c5, c6, c7))
                : val;
    }

    public static short valueOf(long sevenCardMask)
    {
        int index = Eval7FastLookup.index52c7(sevenCardMask);
        return get(values, index); // will be wrong.
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
