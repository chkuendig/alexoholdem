package ao.holdem.ai.bucket.abstraction.access.odds;

import ao.holdem.ai.ai.odds.agglom.hist.StrengthHist;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * Date: Jan 30, 2009
 * Time: 3:21:46 PM
 */
public class BucketOddsTranslator
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(BucketOdds.class);

    private static final String IN_FILE =
            "lookup/bucket/odds_homo.6;odds_homo.144;" +
                "odds_homo.432;odds_homo.1296/eval";
    private static final String OUT_FILE =
            "lookup/bucket/odds_homo.6;odds_homo.144;" +
                "odds_homo.432;odds_homo.1296/eval2";


    //--------------------------------------------------------------------
    public static void main(String[] args) throws IOException
    {
//        translateStrengthToRiver(IN_FILE, OUT_FILE);
        translateRiverToSlim(IN_FILE, OUT_FILE);
    }


    //--------------------------------------------------------------------
    public static void translateStrengthToRiver(
            String inFile,
            String outFile) throws IOException
    {
        StrengthHist[] hist = new StrengthHist[ 1296 ];
        retrieveStrengths(new File(inFile), hist);

        RiverHist[] hist2 = new RiverHist[ hist.length ];
        for (int i = 0; i < hist2.length; i++) {
            hist2[ i ] = new RiverHist( hist[i] );
        }
        persistStrengths(new File(outFile), hist2);
    }


    //--------------------------------------------------------------------
    public static void translateRiverToSlim(
            String inFile,
            String outFile) throws IOException
    {
        RiverHist[] hist = new RiverHist[ 1296 ];
        retrieveStrengths(new File(inFile), hist);

        SlimRiverHist[] hist2 = new SlimRiverHist[ hist.length ];
        for (int i = 0; i < hist2.length; i++) {
            hist2[ i ] = hist[i].slim();
        }
        persistSlims(new File(outFile), hist2);
    }


    //--------------------------------------------------------------------
    private static void persistSlims(
            File file, SlimRiverHist[] strengths) throws IOException
    {
        LOG.debug("persisting slims");

        OutputStream outFile = new FileOutputStream(file);
        TupleOutput out = new TupleOutput();
        for (SlimRiverHist str : strengths)
        {
            SlimRiverHist.BINDING.write(str, out);

            byte asBinary[] = out.getBufferBytes();
            outFile.write( asBinary, 0, out.getBufferLength() );
            out = new TupleOutput(asBinary);
        }

        outFile.close();
    }


    //--------------------------------------------------------------------
    private static boolean retrieveStrengths(
            File file, RiverHist[] hist) throws IOException
    {
        if (! file.canRead()) return false;
        LOG.debug("retrieving strengths");

        InputStream in = new BufferedInputStream(
                                new FileInputStream(file));
        byte[] binStrengths = new byte[ RiverHist.BINDING_SIZE ];

        for (int i = 0; i < hist.length; i++) {
            if (in.read(binStrengths) != RiverHist.BINDING_SIZE) {
                throw new IOException("did not read full amount");
            }

            TupleInput tin = new TupleInput(binStrengths);
            hist[ i ] = RiverHist.BINDING.read( tin );
        }
        in.close();
        return true;
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
            File file, RiverHist[] strengths) throws IOException
    {
        LOG.debug("persisting strengths");

        OutputStream outFile = new FileOutputStream(file);

        TupleOutput out = new TupleOutput();
        for (RiverHist str : strengths)
        {
            RiverHist.BINDING.write(str, out);

            byte asBinary[] = out.getBufferBytes();
            outFile.write( asBinary, 0, out.getBufferLength() );
            out = new TupleOutput(asBinary);
        }

        outFile.close();
    }
}
