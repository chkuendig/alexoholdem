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
        if (hist == null ) {
            RiverHist[] riverHist =
                    computeStrengths(riverBuckets, bucketTree, holes);
            hist = persistStrengths(file, riverHist);
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


    //--------------------------------------------------------------------
    private static SlimRiverHist[] persistStrengths(
            File file, RiverHist[] strengths) throws IOException
    {
        LOG.debug("persisting strengths");

        OutputStream outFile = new BufferedOutputStream(
                                 new FileOutputStream(file));
        SlimRiverHist[] hist = new SlimRiverHist[ strengths.length ];

        TupleOutput out = new TupleOutput();
        for (int i = 0; i < strengths.length; i++)
        {
            RiverHist str = strengths[i];
            hist[ i ]     = str.slim();
            SlimRiverHist.BINDING.write(hist[i], out);

            byte asBinary[] = out.getBufferBytes();
            outFile.write(asBinary, 0, out.getBufferLength());
            out = new TupleOutput(asBinary);
        }
        
        outFile.close();
        return hist;
    }


    //--------------------------------------------------------------------
    private static RiverHist[] computeStrengths(
            final int          riverBuckets,
            final BucketTree   tree,
            final char[][][][] holes)
    {
        LOG.debug("computing strengths");
        final RiverHist[]  hist = new RiverHist[ riverBuckets ];
        for (int i = 0; i < hist.length; i++) {
            hist[ i ] = new RiverHist();
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

                char absoluteRiverBcket = holes
                        [ tree.getHole (  hole.canonIndex() ) ]
                        [ tree.getFlop (  flop.canonIndex() ) ]
                        [ tree.getTurn (  turn.canonIndex() ) ]
                        [ tree.getRiver( river.canonIndex() ) ];
                hist[absoluteRiverBcket].count( river.eval() );

                checkpoint(count[0]++);
            }});
        System.out.println(" DONE!");
        return hist;
    }

    private static void checkpoint(long count) {
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
