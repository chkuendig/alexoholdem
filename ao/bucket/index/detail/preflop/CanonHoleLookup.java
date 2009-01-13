package ao.bucket.index.detail.preflop;

import ao.bucket.abstraction.enumeration.CanonTraverser;
import ao.bucket.index.detail.preflop.CanonHoleDetail.Buffer;
import ao.bucket.index.flop.Flop;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;
import ao.util.io.Slurpy;
import ao.util.misc.Traverser;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Date: Jan 9, 2009
 * Time: 12:30:51 PM
 */
public class CanonHoleLookup
{
    //--------------------------------------------------------------------
    private static final Logger LOG  =
            Logger.getLogger(CanonHoleLookup.class);

    private static final String DIR  = "lookup/canon/detail/";
    private static final File   FILE = new File(DIR, "preflop.detail");


    //--------------------------------------------------------------------
    private static final CanonHoleDetail[] DETAILS =
            retrieveOrComputeDetails();


    //--------------------------------------------------------------------
    private static CanonHoleDetail[] retrieveOrComputeDetails()
    {
        LOG.debug("retrieveOrComputeDetails");

        CanonHoleDetail[] details = retrieveDetails();
        if (details == null)
        {
            details = computeDetails();
            try
            {
                persistDetails(details);
            }
            catch (IOException err)
            {
                LOG.error("while persisting canon hole details", err);
            }
        }
        return details;
    }


    //--------------------------------------------------------------------
    private static CanonHoleDetail[] retrieveDetails()
    {
        byte[] binDetails = Slurpy.slurp(FILE);
        if (binDetails == null || binDetails.length == 0) return null;
        LOG.debug("retrieving details");

        TupleInput        in      = new TupleInput(binDetails);
        CanonHoleDetail[] details =
                new CanonHoleDetail[ Hole.CANONICAL_COUNT ];

        for (int i = 0; i < details.length; i++)
        {
            details[ i ] = CanonHoleDetail.BINDING.read( in );
        }

        return details;
    }


    //--------------------------------------------------------------------
    private static void persistDetails(CanonHoleDetail[] details)
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


    //--------------------------------------------------------------------
    private static CanonHoleDetail[] computeDetails()
    {
        LOG.debug("computing details");
        final CanonHoleDetail.Buffer[] buffers =
                new CanonHoleDetail.Buffer[ Hole.CANONICAL_COUNT ];

        for (Card holeA : Card.VALUES)
        {
            for (Card holeB : Card.VALUES)
            {
                if (holeA.ordinal() >= holeB.ordinal()) continue;
                Hole hole = Hole.valueOf(holeA, holeB);

                Buffer buff = buffers[ hole.canonIndex() ];
                if (buff == null)
                {
                    buff = new Buffer( hole );
                    buffers[ hole.canonIndex() ] = buff;
                }
                buff.REPRESENTS++;
            }
        }

        CanonHoleDetail[] details =
                new CanonHoleDetail[ Hole.CANONICAL_COUNT ];
        for (int i = 0; i < buffers.length; i++)
        {
            System.out.print(".");
            computeFlopDetails( buffers[i] );
            details[ i ] = buffers[i].toDetail();
        }
        System.out.println();

        return details;
    }

    private static void computeFlopDetails(
            final CanonHoleDetail.Buffer buff)
    {
        new CanonTraverser().traverseFlops(
                new long[]{ buff.HOLE.canonIndex() },
                new Traverser<Flop>() {
            public void traverse(Flop flop) {
                if (buff.FIRST_CANON_FLOP == -1) {
                    buff.FIRST_CANON_FLOP = flop.canonIndex();
                }
                buff.FIRST_CANON_FLOP =
                        Math.min(buff.FIRST_CANON_FLOP,
                                 flop.canonIndex());
                buff.CANON_FLOP_COUNT++;
            }
        });
    }


    //--------------------------------------------------------------------
    private CanonHoleLookup() {}


    //--------------------------------------------------------------------
    public static CanonHoleDetail lookup(char canonHole)
    {
        return DETAILS[ canonHole ];
    }
}
