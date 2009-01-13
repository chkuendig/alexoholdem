package ao.bucket.index.detail.flop;

import ao.bucket.abstraction.enumeration.CanonTraverser;
import ao.bucket.index.detail.flop.CanonFlopDetail.Buffer;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.flop.FlopLookup;
import ao.bucket.index.test.Gapper;
import ao.bucket.index.turn.Turn;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;
import static ao.util.data.Arr.swap;
import ao.util.io.Slurpy;
import ao.util.misc.Traverser;
import ao.util.stats.FastIntCombiner;
import ao.util.stats.FastIntCombiner.CombinationVisitor2;
import ao.util.stats.FastIntCombiner.CombinationVisitor3;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Date: Jan 9, 2009
 * Time: 12:34:30 PM
 */
public class CanonFlopLookup
{
    //--------------------------------------------------------------------
    private static final Logger LOG  =
            Logger.getLogger(CanonFlopLookup.class);

    private static final String DIR  = "lookup/canon/detail/";
    private static final File   FILE = new File(DIR, "flop.detail");


    //--------------------------------------------------------------------
    private static final CanonFlopDetail[] DETAILS =
            retrieveOrComputeDetails();


    //--------------------------------------------------------------------
    private static CanonFlopDetail[] retrieveOrComputeDetails()
    {
        LOG.debug("retrieveOrComputeDetails");

        CanonFlopDetail[] details = retrieveDetails();
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
    private static CanonFlopDetail[] retrieveDetails()
    {
        byte[] binDetails = Slurpy.slurp(FILE);
        if (binDetails == null || binDetails.length == 0) return null;
        LOG.debug("retrieving details");

        TupleInput in      = new TupleInput(binDetails);
        CanonFlopDetail[] details =
                new CanonFlopDetail[ FlopLookup.CANONICAL_COUNT ];

        for (int i = 0; i < details.length; i++)
        {
            details[ i ] = CanonFlopDetail.BINDING.read( in );
        }

        return details;
    }


    //--------------------------------------------------------------------
    private static void persistDetails(CanonFlopDetail[] details)
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


    //--------------------------------------------------------------------
    private static CanonFlopDetail[] computeDetails()
    {
        LOG.debug("computing details");
        final CanonFlopDetail.Buffer[] buffers =
                new CanonFlopDetail.Buffer[
                        FlopLookup.CANONICAL_COUNT ];

        final Gapper seenHoles = new Gapper();
        final Card[] cards     = Card.values();
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length).combine(
                new CombinationVisitor2() {
            public void visit(int holeA, int holeB) {
                Hole hole = Hole.valueOf(
                        cards[holeA], cards[holeB]);

                if (seenHoles.get( hole.canonIndex() )) return;
                seenHoles.set( hole.canonIndex() );

                swap(cards, holeB, 51  );
                swap(cards, holeA, 51-1);

                iterateFlops(hole, cards, buffers);

                swap(cards, holeA, 51-1);
                swap(cards, holeB, 51  );
            }
        });

        LOG.debug("computing turn info");
        CanonFlopDetail[] details =
                new CanonFlopDetail[ FlopLookup.CANONICAL_COUNT ];
        for (int i = 0; i < buffers.length; i++)
        {
            if (i % 10000 == 0) System.out.print(".");
//            computeFlopDetails( buffers[i], i );
            details[ i ] = buffers[i].toDetail();
        }
        System.out.println();
        return details;
    }

    public static void iterateFlops(
            final Hole     hole,
            final Card[]   cards,
            final Buffer[] buffers)
    {
        System.out.println(hole);
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length - 2).combine(
                new CombinationVisitor3() {
            public void visit(int flopA, int flopB, int flopC)
            {
                Flop flop = hole.addFlop(
                        cards[flopA], cards[flopB], cards[flopC]);
                int index = flop.canonIndex();

                Buffer buff = buffers[ index ];
                if (buff == null)
                {
                    buff = new Buffer( flop );
                    buffers[ index ] = buff;
                }
                buff.REPRESENTS++;
            }});
    }


    //--------------------------------------------------------------------
    private static void computeFlopDetails(
            final Buffer buff,
            final int    canonFlopIndex)
    {
        new CanonTraverser().traverseTurns(
                new long[]{ canonFlopIndex },
                new Traverser<Turn>() {
            public void traverse(Turn turn) {
                if (buff.FIRST_CANON_TURN == -1) {
                    buff.FIRST_CANON_TURN = turn.canonIndex();
                }
                buff.FIRST_CANON_TURN =
                        Math.min(buff.FIRST_CANON_TURN,
                                 turn.canonIndex());
                buff.CANON_TURN_COUNT++;
            }
        });
    }


    //--------------------------------------------------------------------
    private CanonFlopLookup() {}


    //--------------------------------------------------------------------
    public static CanonFlopDetail lookup(int canonFlop)
    {
        return DETAILS[ canonFlop ];
    }
}
