package ao.bucket.index.detail.turn;

import ao.bucket.index.turn.TurnLookup;
import ao.util.io.Slurpy;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Date: Jan 21, 2009
 * Time: 11:36:21 AM
 */
public class TurnDetailPersist
{
    //--------------------------------------------------------------------
    private static final Logger LOG  =
            Logger.getLogger(TurnDetailPersist.class);

    private static final String DIR  = "lookup/canon/detail/";
    private static final File FILE = new File(DIR, "turn.detail");

    private TurnDetailPersist() {}


    //--------------------------------------------------------------------
    public static CanonTurnDetail[] retrieveDetails()
    {
        byte[] binDetails = Slurpy.slurp(FILE);
        if (binDetails == null || binDetails.length == 0) return null;
        LOG.debug("retrieving details");

        TupleInput        in      = new TupleInput(binDetails);
        CanonTurnDetail[] details =
                new CanonTurnDetail[ TurnLookup.CANONICAL_COUNT ];

        for (int i = 0; i < details.length; i++)
        {
            details[ i ] = CanonTurnDetail.BINDING.read( i, in );
        }

        return details;
    }


    //--------------------------------------------------------------------
    public static void persistDetails(CanonTurnDetail[] details)
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


    private static void doPersistDetails(CanonTurnDetail[] details)
            throws IOException
    {
        LOG.debug("persisting details");

        OutputStream outFile =
                new FileOutputStream(FILE);

        TupleOutput out = new TupleOutput();
        for (CanonTurnDetail detail : details)
        {
            CanonTurnDetail.BINDING.write(detail, out);

            byte asBinary[] = out.getBufferBytes();
            outFile.write( asBinary, 0, out.getBufferLength() );
            out = new TupleOutput(asBinary);
        }

        outFile.close();
    }
}
