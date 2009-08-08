package ao.regret.holdem.parallel;

import ao.holdem.model.Round;
import ao.regret.holdem.IterativeMinimizer;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: alex
 * Date: 26-Apr-2009
 * Time: 11:04:26 PM
 */
public class ParallelMinimizer implements IterativeMinimizer
{
    //--------------------------------------------------------------------
    private static final int PROCESSORS  = Math.max(1,
            Runtime.getRuntime().availableProcessors() - 1);

    private static final int WINDOW_SIZE = 128;
    private static final int BUFFER_SIZE = PROCESSORS;


    //--------------------------------------------------------------------
//    private final ForkJoinPool EXEC = new ForkJoinPool();
    private final ExecutorService EXEC =
            Executors.newFixedThreadPool( PROCESSORS );
//            Executors.newCachedThreadPool();

    private final char  windowBuckets[][][]    =
            new char[ WINDOW_SIZE ][ 2 ][ Round.COUNT ];
    private       int   nextWindow = 0;

    private       int[] uniqueIndexes = new int[ BUFFER_SIZE ];
    private       int   nextUnique    = 0;

    private final BitSet uniqueHoles  = new BitSet();


//    private final LongBitSet bufferedHoles =
//            new LongBitSet(HoleLookup.CANONS);
    private final IterativeMinimizer DELEGET;


    //--------------------------------------------------------------------
    public ParallelMinimizer(
            IterativeMinimizer deleget)
    {
        DELEGET = deleget;
    }


    //--------------------------------------------------------------------
    public void iterate(
            char[] absDealerBuckets,
            char[] absDealeeBuckets)
    {
        if (nextUnique == uniqueIndexes.length ||
                nextWindow == windowBuckets.length) {
//            drainUniques();
//            loadUniques();
            flush();
        }

        enBuffer(new char[][]{
                absDealerBuckets, absDealeeBuckets});
    }



    //--------------------------------------------------------------------
    public void flush()
    {
        drainUniques();
        while (nextWindow > 0)
        {
            loadUniques();
            drainUniques();
        }
    }


    //--------------------------------------------------------------------
    private void drainUniques()
    {
        Collection<Callable<Void>> todo =
                new ArrayList<Callable<Void>>(nextUnique);

        while (--nextUnique >= 0) {
            final char[][] bucketSequences =
                    windowBuckets[ uniqueIndexes[nextUnique] ];

            windowBuckets[ uniqueIndexes[nextUnique] ] =
                    windowBuckets[ --nextWindow ];

            todo.add(new Callable<Void>() {
                public Void call() throws Exception {
                    DELEGET.iterate(
                            bucketSequences[0],
                            bucketSequences[1]);
                    return null;
                }
            });
        }

        clearUniques();

        try {
            EXEC.invokeAll(todo);
        } catch (InterruptedException e) {
            throw new Error( e );
        }
    }


    //--------------------------------------------------------------------
    private void loadUniques()
    {
        for (int i = 0; i < nextWindow; i++)
        {
            if (isUnique( windowBuckets[i] )) {
                if (uniquesFull()) return;
                addUnique(i);
            }
        }
    }


    //--------------------------------------------------------------------
    private void enBuffer(char[][] bucketSequences)
    {
        windowBuckets[ nextWindow++ ] = bucketSequences;

        if (!uniquesFull() && isUnique(bucketSequences)) {
            addUnique(nextWindow - 1);
        }
    }


    //--------------------------------------------------------------------
    private boolean isUnique(char[][] bucketSequences)
    {
        return ! (uniqueHoles.get(bucketSequences[0][0]) ||
                  uniqueHoles.get(bucketSequences[1][0]));
    }

    private void addUnique(int windowIndex)
    {
        uniqueIndexes[ nextUnique++ ] = windowIndex;
        uniqueHoles.set( windowBuckets[windowIndex][0][0] );
        uniqueHoles.set( windowBuckets[windowIndex][1][0] );
    }

    private void clearUniques()
    {
        nextUnique = 0;
        uniqueHoles.clear();
    }


    //--------------------------------------------------------------------
    private boolean uniquesFull()
    {
        return nextUnique == uniqueIndexes.length;
    }
}
