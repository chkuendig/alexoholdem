package ao.holdem.engine.detail.preflop;

import ao.Infrastructure;
import ao.holdem.model.canon.hole.CanonHole;
import ao.util.io.Dirs;
import ao.util.persist.PersistentBytes;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Date: Jan 13, 2009
 * Time: 12:44:19 PM
 */
public class HoleDetailDao
{
    //--------------------------------------------------------------------
    private static final Logger LOG  =
            Logger.getLogger(HoleDetailDao.class);


    //--------------------------------------------------------------------
    private HoleDetailDao() {}


    //--------------------------------------------------------------------
    private static final File    DIR  = Dirs.get(
            Infrastructure.path("lookup/canon/detail"));
    private static final File    FILE = new File(DIR, "preflop.detail");

    private static final boolean isMomoized = FILE.exists();


    //--------------------------------------------------------------------
    public static boolean detailsMomoized()
    {
        return isMomoized;
    }


    //--------------------------------------------------------------------
    public static CanonHoleDetail[] retrieveDetails()
    {
        byte[] binDetails = PersistentBytes.retrieve(FILE);
        if (binDetails == null || binDetails.length == 0) return null;
        LOG.debug("retrieving details");

        TupleInput in      = new TupleInput(binDetails);
        CanonHoleDetail[] details =
                new CanonHoleDetail[CanonHole.CANONS];

        for (int i = 0; i < details.length; i++)
        {
            details[ i ] = CanonHoleDetail.BINDING.read( in );
        }

        LOG.debug("done");
        return details;
    }


    //--------------------------------------------------------------------
    public static void persistDetails(CanonHoleDetail[] details)
    {
        try
        {
            doPersistDetails( details );
        }
        catch (IOException err)
        {
            LOG.error("while persisting canon hole details");
            err.printStackTrace();
        }
    }

    private static void doPersistDetails(CanonHoleDetail[] details)
            throws IOException
    {
        LOG.debug("persisting details");

        OutputStream outFile =
                new FileOutputStream(FILE);

        TupleOutput out = new TupleOutput();
        for (CanonHoleDetail detail : details)
        {
            CanonHoleDetail.BINDING.write(detail, out);

            byte asBinary[] = out.getBufferBytes();
            outFile.write( asBinary, 0, out.getBufferLength() );
            out = new TupleOutput(asBinary);
        }

        outFile.close();
    }
}
