package ao.regret.holdem.parallel;

import ao.bucket.abstraction.access.tree.LongByteList;
import ao.regret.holdem.IterativeMinimizer;
import org.apache.log4j.Logger;

import java.util.*;
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
    private static final Logger LOG =
            Logger.getLogger(ParallelMinimizer.class);


    //--------------------------------------------------------------------
    private static final int PROCESSORS  = Math.max(1,
            Runtime.getRuntime().availableProcessors() - 1);

    private static final int BUFFER_SIZE = 2;
    private static final int WINDOW_SIZE = BUFFER_SIZE;


    //--------------------------------------------------------------------
//    private final ForkJoinPool EXEC = new ForkJoinPool();
    private final ExecutorService EXEC =
            Executors.newFixedThreadPool( PROCESSORS );
//            Executors.newCachedThreadPool();

    private final List<char[][]> windowBuckets =
            new LinkedList<char[][]>();

    private final IterativeMinimizer DELEGET;
    private final LongByteList       HOLE_BUCKETS;


    //--------------------------------------------------------------------
    public ParallelMinimizer(
              IterativeMinimizer deleget
            , LongByteList       holeBuckets
            )
    {
        DELEGET      = deleget;
        HOLE_BUCKETS = holeBuckets;
    }


    //--------------------------------------------------------------------
    public void iterate(
            char[] absDealerBuckets,
            char[] absDealeeBuckets)
    {
        if (windowBuckets.size() >= WINDOW_SIZE) {
            flush();
        }

        windowBuckets.add(new char[][]{
                absDealerBuckets, absDealeeBuckets});
    }



    //--------------------------------------------------------------------
    public void flush()
    {
        while (! windowBuckets.isEmpty()) {
            flushGreedy();
        }
    }

    private void flushGreedy()
    {
        LOG.debug("flushGreedy");

        Collection<Callable<Void>> todo =
                new ArrayList<Callable<Void>>();

        BitSet seen = new BitSet();
        for (Iterator<char[][]> bucketItr = windowBuckets.iterator();
                                bucketItr.hasNext() &&
                                    todo.size() < BUFFER_SIZE;)
        {
            final char[][] bucketSequences = bucketItr.next();

            if (! (seen.get( bucketSequences[0][0] ) ||
                   seen.get( bucketSequences[1][0] )))
            {
                   seen.set( bucketSequences[0][0] );
                   seen.set( bucketSequences[1][0] );

                LOG.debug("enqueueing " + display(bucketSequences[0]) +
                                  " | " + display(bucketSequences[1]));
                todo.add(new Callable<Void>() {
                    public Void call() throws Exception {
                        LOG.debug("iterating " +
                                        display(bucketSequences[0]) +
                                " | " + display(bucketSequences[1]));
                        DELEGET.iterate(
                                bucketSequences[0],
                                bucketSequences[1]);
                        return null;
                    }
                });

                bucketItr.remove();
            }
        }

        try {
            EXEC.invokeAll(todo);
        } catch (InterruptedException e) {
            throw new Error( e );
        }
    }

    private String display(char[] asChar) {
        StringBuilder str = new StringBuilder();

        int[] asInt = fromChar(asChar);
        str.append(Arrays.toString(asInt));
        str.append("\t");
        str.append(HOLE_BUCKETS.get(asInt[0]));

        return str.toString();
    }

    private int[] fromChar(char[] asChar) {
        int[] asInt = new int[ asChar.length ];
        for (int i = 0; i < asChar.length; i++) {
            asInt[ i ] = asChar[ i ];
        }
        return asInt;
    }
}
