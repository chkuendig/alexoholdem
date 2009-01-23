package ao.bucket.index.flop;

import ao.bucket.index.hole.CanonHole;
import ao.bucket.index.hole.HoleLookup;
import ao.holdem.model.card.Card;
import static ao.util.data.Arr.swap;
import ao.util.math.stats.Combiner;
import ao.util.persist.PersistentInts;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * Date: Aug 16, 2008
 * Time: 2:48:07 PM
 */
public class FlopLookup
{
    //--------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(FlopLookup.class);


    //--------------------------------------------------------------------
    public static final int CANONS = 1286792;


    //--------------------------------------------------------------------
    private static final String OFFSET_FILE =
            "lookup/canon/flop.offsets.cache";
    private static final int    OFFSETS[][] =
            retrieveOrCalculateOffsets();


    private static int[][] retrieveOffsets()
    {
        LOG.info("attempting to retrieveOffsets");
        int flat[] = PersistentInts.retrieve(OFFSET_FILE);
        if (flat == null) return null;

        LOG.info("indexing retrieved offsets");
        int offsets[][] =
                new int[HoleLookup.CANONS]
                       [   FlopCase.VALUES.length   ];

        for (int i = 0; i < flat.length; i++)
        {
            int row = i / FlopCase.VALUES.length;
            int col = i % FlopCase.VALUES.length;

            offsets[ row ][ col ] = flat[ i ];
        }

        return offsets;
    }

    private static int[][] retrieveOrCalculateOffsets()
    {
        int offsets[][] = retrieveOffsets();
        if (offsets == null)
        {
            offsets = calculateOffsets();
            storeOffsets( offsets );
        }
        
        LOG.info("finished retrieveOrCalculateOffsets");
        return offsets;
    }

    private static void storeOffsets(int offsets[][])
    {
        LOG.info("storing offsets");
        int flat[] = new int[ offsets.length * offsets[0].length ];

        int index = 0;
        for (int subOffsets[] : offsets)
        {
            for (int offset : subOffsets)
            {
                flat[ index++ ] = offset;
            }
        }
        PersistentInts.persist(flat, OFFSET_FILE);
    }

    private static int[][] calculateOffsets()
    {
        LOG.info("calculating offsets");
        int offsets[][] = new int[HoleLookup.CANONS][];

        int  offset  = 0;
        Card cards[] = Card.values();
        for (Card holeCards[] : new Combiner<Card>(Card.VALUES, 2))
        {
            CanonHole hole = HoleLookup.lookup(
                            holeCards[0], holeCards[1]);

            int subOffsets[] = offsets[ hole.canonIndex() ];
            if (subOffsets != null) continue;
            subOffsets = new int[ FlopCase.values().length ];
            Arrays.fill(subOffsets, -1);

            offsets[ hole.canonIndex() ] = subOffsets;

            swap(cards, holeCards[1].ordinal(), 51  );
            swap(cards, holeCards[0].ordinal(), 51-1);

            for (Card flopCards[] : new Combiner<Card>(cards, 50, 3))
            {
                Flop isoFlop = hole.addFlop(
                                    flopCards[0],
                                    flopCards[1],
                                    flopCards[2]);

                FlopCase flopCase = isoFlop.flopCase();
                if (subOffsets[ flopCase.ordinal() ] != -1) continue;

                subOffsets[ flopCase.ordinal() ] = offset;
                offset += flopCase.size();
            }

            swap(cards, holeCards[0].ordinal(), 51-1);
            swap(cards, holeCards[1].ordinal(), 51  );
        }

        return offsets;
    }

    public static void main(String[] args)
    {
        for (int subOffsets[] : OFFSETS)
        {
            System.out.println(
                    Arrays.toString(
                            subOffsets));
        }
    }


    //--------------------------------------------------------------------
    public static int globalOffset(CanonHole hole, FlopCase flopCase)
    {
        return OFFSETS[ hole.canonIndex()  ]
                      [ flopCase.ordinal() ];
    }
}
