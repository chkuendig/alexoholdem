package ao.bucket.abstraction.access.odds;

import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.index.enumeration.HandEnum;
import ao.bucket.index.enumeration.PermisiveFilter;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.hole.CanonHole;
import ao.bucket.index.river.River;
import ao.bucket.index.turn.Turn;
import ao.odds.agglom.hist.StrengthHist;
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

        File           file = new File(dir, STR_FILE);
        StrengthHist[] hist = new StrengthHist[ riverBuckets ];
        if (! retrieveStrengths(file, hist)) {
            computeStrengths(hist, bucketTree, holes);
            persistStrengths(file, hist);
        }
        return new BucketOdds(hist);
    }


    //--------------------------------------------------------------------
    private static boolean retrieveStrengths(
            File file, StrengthHist[] hist) throws IOException
    {
        if (! file.canRead()) return false;
        LOG.debug("retrieving strengths");

        InputStream in = new BufferedInputStream(
                                new FileInputStream(file));
        byte[] binStrengths = new byte[ StrengthHist.BINDING_SIZE ];

        for (int i = 0; i < hist.length; i++) {
            if (in.read(binStrengths) != StrengthHist.BINDING_SIZE) {
                throw new IOException("did not read full amount");
            }

            TupleInput tin = new TupleInput(binStrengths);
            hist[ i ] = StrengthHist.BINDING.read( tin );
        }
        in.close();
        return true;
    }


    //--------------------------------------------------------------------
    private static void persistStrengths(
            File file, StrengthHist[] strengths) throws IOException
    {
        LOG.debug("persisting strengths");

        OutputStream outFile = new FileOutputStream(file);

        TupleOutput out = new TupleOutput();
        for (StrengthHist str : strengths)
        {
            StrengthHist.BINDING.write(str, out);

            byte asBinary[] = out.getBufferBytes();
            outFile.write( asBinary, 0, out.getBufferLength() );
            out = new TupleOutput(asBinary);
        }

        outFile.close();
    }


    //--------------------------------------------------------------------
    private static void computeStrengths(
            final StrengthHist[] hist,
            final BucketTree     tree,
            final char[][][][]   holes)
    {
        LOG.debug("computing strengths");
        for (int i = 0; i < hist.length; i++) {
            hist[ i ] = new StrengthHist();
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
    }

    private static void checkpoint(long count) {
        if ((count       % (1000 * 1000)) == 0)
            System.out.print(".");

        if (((count + 1) % (1000 * 1000 * 50)) == 0)
            System.out.println();
    }
    

    //--------------------------------------------------------------------
    private final StrengthHist[] HIST;

    private BucketOdds(StrengthHist[] hist)
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
        StrengthHist h = HIST[index];
        return h.mean() + " with " + h.totalCount();
    }

    public StrengthHist strength(char index)
    {
        return HIST[ index ];
    }
}
