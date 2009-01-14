package ao.bucket.index.detail.flop;

import ao.bucket.index.flop.FlopLookup;
import ao.util.io.Slurpy;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Date: Jan 14, 2009
 * Time: 11:09:28 AM
 */
public class FlopLookupPersist
{
    //--------------------------------------------------------------------
    private static final Logger LOG  =
            Logger.getLogger(FlopLookupPersist.class);

    private static final String DIR  = "lookup/canon/detail/";
    private static final File FILE = new File(DIR, "flop.detail");


    //--------------------------------------------------------------------
    public static CanonFlopDetail[] retrieveDetails()
    {
        byte[] binDetails = Slurpy.slurp(FILE);
        if (binDetails == null || binDetails.length == 0) return null;
        LOG.debug("retrieving details");

        TupleInput in      = new TupleInput(binDetails);
        CanonFlopDetail[] details =
                new CanonFlopDetail[ FlopLookup.CANONICAL_COUNT ];

        for (int i = 0; i < details.length; i++)
        {
            details[ i ] = CanonFlopDetail.BINDING.read( i, in );
        }

        return details;
    }


    //--------------------------------------------------------------------
    public static void persistDetails(CanonFlopDetail[] details)
    {
        try
        {
            doPersistDetails( details );
        }
        catch (IOException err)
        {
            LOG.error("while persisting canon details");
            err.printStackTrace();
        }
    }


    private static void doPersistDetails(CanonFlopDetail[] details)
            throws IOException
    {
        LOG.debug("persisting details");

        OutputStream outFile =
                new FileOutputStream(FILE);

        TupleOutput out = new TupleOutput();
        for (CanonFlopDetail detail : details)
        {
            CanonFlopDetail.BINDING.write(detail, out);

            byte asBinary[] = out.getBufferBytes();
            outFile.write( asBinary, 0, out.getBufferLength() );
            out = new TupleOutput(asBinary);
        }

        outFile.close();
    }
}
