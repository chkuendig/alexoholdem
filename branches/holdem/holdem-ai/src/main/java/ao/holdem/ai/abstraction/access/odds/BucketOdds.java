package ao.holdem.ai.abstraction.access.odds;

import ao.holdem.ai.abstraction.access.AbsBucketStore;
import ao.holdem.ai.abstraction.access.BucketDecoder;
import ao.holdem.ai.abstraction.access.tree.BucketTree;
import ao.holdem.model.canon.river.River;
import ao.holdem.engine.detail.river.RiverEvalLookup;
import ao.util.time.Progress;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Iterator;

/**
 * Date: Jan 29, 2009
 * Time: 12:17:17 PM
 */
public class BucketOdds implements IBucketOdds
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(BucketOdds.class);

    private static final String STR_FILE = "eval";
    private static final int    BUFFER   = 100000;


    //--------------------------------------------------------------------
    public static BucketOdds retrieve(
            File dir, BucketDecoder decoder)
    {
        SlimRiverHist[] hist = retrieveStrengths(
                                   dir, decoder.riverBucketCount());
        int             off  = offset(hist);

        return (hist == null || off != hist.length)
               ? null : new BucketOdds(hist);
    }
    public static BucketOdds retrieveOrCompute(
            File dir, BucketTree bucketTree, BucketDecoder decoder)
    {
        int riverBuckets = decoder.riverBucketCount();

        SlimRiverHist[] hist = retrieveStrengths(dir, riverBuckets);
        int             off  = offset(hist);
        if (hist == null || off != hist.length) {
            hist = computeAndPersistStrengths(off,
                    riverBuckets, bucketTree, decoder, dir);
        }
        return new BucketOdds(hist);
    }


    //--------------------------------------------------------------------
    private static SlimRiverHist[] retrieveStrengths(
            File dir, int riverBuckets) {
        try {
            return doRetrieveStrengths(dir, riverBuckets);
        } catch (IOException e) {
            throw new Error( e );
        }
    }
    private static SlimRiverHist[] doRetrieveStrengths(
            File dir, int riverBuckets) throws IOException
    {
        File file = new File(dir, STR_FILE);
        if (! file.canRead()) return null;
        LOG.debug("retrieving strengths");

        SlimRiverHist[] hist = new SlimRiverHist[ riverBuckets ];
        InputStream in = new BufferedInputStream(
                                new FileInputStream(file));
        byte[] binStrengths =
                new byte[ SlimRiverHist.BINDING_MAX_SIZE ];
        //noinspection ResultOfMethodCallIgnored
        in.read(binStrengths, 0, binStrengths.length);

        long processed = 0, toProcess = file.length();
        for (int i = 0, offset; i < hist.length; i++) {
            TupleInput tin = new TupleInput(binStrengths);
            hist[ i ]      = SlimRiverHist.BINDING.read( tin );
            offset         = hist[ i ].bindingSize();

            processed += offset;
            if (processed == toProcess) break;

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
            SlimRiverHist[] bucketHist, File file)
    {
        try
        {
            doPersistStrengths(bucketHist, file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private static void doPersistStrengths(
            SlimRiverHist[] bucketHist, File file) throws IOException
    {
        LOG.debug("persisting strengths");

        OutputStream outFile = new BufferedOutputStream(
                                 new FileOutputStream(file, true));

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
            int           initialOffset,
            int           riverBuckets,
            BucketTree    tree,
            BucketDecoder decoder,
            File          dir)
    {
        LOG.debug("computing strengths");
        File            outFile = new File(dir, STR_FILE);
        SlimRiverHist[] hist    = new SlimRiverHist[ riverBuckets ];

        if (riverBuckets <= BUFFER) {
            computeAllStrengths(hist, tree, decoder);
            persistStrengths(hist, outFile);
        } else {
            AbsBucketStore absBuckets =
                    new AbsBucketStore(dir, tree, decoder);
            for (int offset = initialOffset;
                     offset < riverBuckets;
                     offset += BUFFER)
            {
                computeStrengths(hist,
                                 offset,
                                 Math.min(BUFFER,
                                          riverBuckets - offset),
                                 absBuckets);
                persistStrengths(hist, outFile);
            }
        }

        LOG.debug(" DONE!");
        return hist;
    }
    private static void computeStrengths(
            final SlimRiverHist[] hist,
            final int             offset,
            final int             length,
            final AbsBucketStore absBuckets)
    {
        final RiverHist[]  histBuff = new RiverHist[ length ];
        for (int i = 0; i < histBuff.length; i++) {
            histBuff[ i ] = new RiverHist();
        }

//        final LongBitSet allowRivers =
//                computeAllowedRivers(
//                        offset, length, tree, holes);

        LOG.debug("computing strengths for allowed");
        final Progress progress = new Progress(length * 100000);
//        RiverEvalLookup.traverse(allowRivers,
//                new AbsVisitor() {public void traverse(
//                        long river, short strength, byte count) {
//
//            char absoluteRiverBucket = bucketOf(tree, holes, river);
//            histBuff[absoluteRiverBucket - offset]
//                     .count( strength, count );
//            checkpoint(progress[0]++);
//        }});

        final Iterator<Character> absBucketItr = absBuckets.iterator();
        RiverEvalLookup.traverse(new RiverEvalLookup.AbsVisitor() {public void traverse(
                     long river, short strength, byte count) {
            char absoluteRiverBucket =
                    absBucketItr.next();

            if (offset <= absoluteRiverBucket &&
                          absoluteRiverBucket < (offset + length)) {

                histBuff[absoluteRiverBucket - offset]
                         .count( strength, count );
                progress.checkpoint();
            }
        }});

        for (int i = 0; i < length; i++) {
            hist[offset + i] = histBuff[i].slim();
        }
    }


    //--------------------------------------------------------------------
    private static void computeAllStrengths(
            final SlimRiverHist[] hist,
            final BucketTree      tree,
            final BucketDecoder   decoder)
    {
        final RiverHist[]  histBuff = new RiverHist[ hist.length ];
        for (int i = 0; i < histBuff.length; i++) {
            histBuff[ i ] = new RiverHist();
        }

        LOG.debug("computing all strengths");
        final Progress progress = new Progress(River.CANONS);
        RiverEvalLookup.traverse(new RiverEvalLookup.AbsVisitor() {public void traverse(
                     long river, short strength, byte count) {
            char absoluteRiverBucket =
                    AbsBucketStore.bucketOf(tree, decoder, river);
            histBuff[absoluteRiverBucket]
                     .count( strength, count );
            progress.checkpoint();
        }});

        for (int i = 0; i < hist.length; i++) {
            hist[i] = histBuff[i].slim();
        }
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


    //--------------------------------------------------------------------
    public IBucketOdds cache()
    {
        return new BucketOddsCache(this, (char) HIST.length);
    }
}
