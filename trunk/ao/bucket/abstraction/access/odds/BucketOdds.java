package ao.bucket.abstraction.access.odds;

import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.index.enumeration.HandEnum;
import ao.bucket.index.enumeration.PermisiveFilter;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.hole.CanonHole;
import ao.bucket.index.river.River;
import ao.bucket.index.turn.Turn;
import ao.util.misc.Traverser;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * Date: Jan 29, 2009
 * Time: 12:17:17 PM
 */
public class BucketOdds
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(BucketOdds.class);

    private static final String STR_FILE = "eval";
    private static final int    BUFFER   = 10 * 1000;


    //--------------------------------------------------------------------
    public static BucketOdds retrieveOrCompute(
            File dir, BucketTree bucketTree, char[][][][] holes)
    {
        try {
            return doRetrieveOrCompute(dir, bucketTree, holes);
        } catch (IOException e) {
            LOG.error("unable to retrieveOrCompute");
            e.printStackTrace();
            return null;
        }
    }
    private static BucketOdds doRetrieveOrCompute(
            File dir, BucketTree bucketTree, char[][][][] holes)
            throws IOException
    {
        char[][][] flops  =  holes[  holes.length - 1 ];
        char[][]   turns  =  flops[  flops.length - 1 ];
        char[]     rivers =  turns[  turns.length - 1 ];
        int  riverBuckets = rivers[ rivers.length - 1 ] + 1;

        File            file = new File(dir, STR_FILE);
        SlimRiverHist[] hist = retrieveStrengths(file, riverBuckets);
        int             off  = offset(hist);
        if (hist == null || off != hist.length) {
            hist = computeAndPersistStrengths(off,
                    riverBuckets, bucketTree, holes, file);
        }
        return new BucketOdds(hist);
    }


    //--------------------------------------------------------------------
    private static SlimRiverHist[] retrieveStrengths(
            File file, int riverBuckets) throws IOException
    {
        if (! file.canRead()) return null;
        LOG.debug("retrieving strengths");

        SlimRiverHist[] hist = new SlimRiverHist[ riverBuckets ];
        InputStream in = new BufferedInputStream(
                                new FileInputStream(file));
        byte[] binStrengths =
                new byte[ SlimRiverHist.BINDING_MAX_SIZE ];
        //noinspection ResultOfMethodCallIgnored
        in.read(binStrengths, 0, binStrengths.length);

        for (int i = 0, offset; i < hist.length; i++) {
            TupleInput tin = new TupleInput(binStrengths);
            hist[ i ]      = SlimRiverHist.BINDING.read( tin );
            offset         = hist[ i ].bindingSize();

            System.arraycopy(
                    binStrengths,
                    offset,
                    binStrengths,
                    0,
                    binStrengths.length - offset);
            //noinspection ResultOfMethodCallIgnored
            in.read(binStrengths, binStrengths.length - offset, offset);
        }
        in.close();
        return hist;
    }

    private static int offset(SlimRiverHist[] hist)
    {
        if (hist == null) return 0;
        for (int i = 0; i < hist.length; i++){
            if (hist[i] == null) return i;
        }
        return hist.length;
    }



    //--------------------------------------------------------------------
    private static void persistStrengths(
            File file, SlimRiverHist[] bucketHist) throws IOException
    {
        LOG.debug("persisting strengths");

        OutputStream outFile = new BufferedOutputStream(
                                 new FileOutputStream(file));

        TupleOutput out = new TupleOutput();
        for (SlimRiverHist hist : bucketHist)
        {
            if (hist == null) break;
            SlimRiverHist.BINDING.write(hist, out);

            byte asBinary[] = out.getBufferBytes();
            outFile.write(asBinary, 0, out.getBufferLength());
            out = new TupleOutput(asBinary);
        }
        
        outFile.close();
    }


    //--------------------------------------------------------------------
    private static SlimRiverHist[] computeAndPersistStrengths(
            int          initialOffset,
            int          riverBuckets,
            BucketTree   tree,
            char[][][][] holes,
            File         outFile) throws IOException
    {
        LOG.debug("computing strengths");
        final SlimRiverHist[] hist = new SlimRiverHist[ riverBuckets ];

        for (int offset = initialOffset;
                 offset < riverBuckets;
                 offset += BUFFER)
        {
            computeStrengths(hist,
                             offset,
                             Math.min(BUFFER,
                                      riverBuckets - offset),
                             tree, holes);
            persistStrengths(outFile, hist);
        }

        System.out.println(" DONE!");
        return hist;
    }
    private static void computeStrengths(
            final SlimRiverHist[] hist,
            final int             offset,
            final int             length,
            final BucketTree      tree,
            final char[][][][]    holes)
    {
        final RiverHist[]  histBuff = new RiverHist[ length ];
        for (int i = 0; i < histBuff.length; i++) {
            histBuff[ i ] = new RiverHist();
        }

        final long[] count = {0};
        HandEnum.rivers(
                new PermisiveFilter<CanonHole>(),
                new PermisiveFilter<Flop>(),
                new PermisiveFilter<Turn>(),
                new PermisiveFilter<River>(),
                new Traverser<River>() {
            public void traverse(River river) {
                Turn      turn = river.turn();
                Flop      flop = turn.flop();
                CanonHole hole = flop.hole();

                char absoluteRiverBucket = holes
                        [ tree.getHole (  hole.canonIndex() ) ]
                        [ tree.getFlop (  flop.canonIndex() ) ]
                        [ tree.getTurn (  turn.canonIndex() ) ]
                        [ tree.getRiver( river.canonIndex() ) ];
                if (offset <= absoluteRiverBucket &&
                              absoluteRiverBucket < (offset + length)) {

                    histBuff[absoluteRiverBucket - offset]
                                .count( river.eval() );
                    checkpoint(count[0]++);
                }
            }});

        for (int i = 0; i < length; i++) {
            hist[offset + i] = histBuff[i].slim();
        }
    }

    private static void checkpoint(long count) {
        if (count == 0) {
            System.out.println("starting calculation cycle");
            return;
        }

        if ((count       % (1000 * 1000)) == 0)
            System.out.print(".");

        if (((count + 1) % (1000 * 1000 * 50)) == 0)
            System.out.println();
    }
    

    //--------------------------------------------------------------------
    private final SlimRiverHist[] HIST;

//    private BucketOdds(SlimRiverHist[] hist)
//    {
//        HIST = hist;
//    }

    private BucketOdds(SlimRiverHist[] hist)
    {
        HIST = hist;
    }


    //--------------------------------------------------------------------
    public double nonLossProb(char index, char vsIndex)
    {
        return HIST[index].nonLossProb( HIST[vsIndex] );
    }

    public String status(char index)
    {
        SlimRiverHist h = HIST[index];
        return h.mean() + " with " + h.totalCount();
    }

    public SlimRiverHist strength(char index)
    {
        return HIST[ index ];
    }
}
