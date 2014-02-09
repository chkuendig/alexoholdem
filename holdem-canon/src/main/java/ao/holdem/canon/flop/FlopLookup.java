package ao.holdem.canon.flop;

import ao.Infrastructure;
import ao.holdem.canon.hole.CanonHole;
import ao.holdem.canon.hole.HoleLookup;
import ao.holdem.model.card.Card;
import ao.util.data.Arrs;
import ao.util.io.Dirs;
import ao.util.math.stats.Combiner;
import ao.util.persist.PersistentInts;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Arrays;

/**
 * Date: Aug 16, 2008
 * Time: 2:48:07 PM
 */
/*package-private*/ class FlopLookup
{
    //--------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(FlopLookup.class);


    //--------------------------------------------------------------------
    private static final File OFFSET_FILE = new File(
            Dirs.get(Infrastructure.path("lookup/canon")),
                     "flop.offsets.cache");
    private static final int  OFFSETS[][] =
            retrieveOrCalculateOffsets();


    //--------------------------------------------------------------------
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


    private static int[][] retrieveOffsets()
    {
        LOG.info("attempting to retrieveOffsets");
        int flat[] = PersistentInts.retrieve(OFFSET_FILE);
        if (flat == null) return null;

        LOG.info("indexing retrieved offsets");
        int offsets[][] =
                new int[ CanonHole.CANONS      ]
                       [ FlopCase.VALUES.length ];

        for (int i = 0; i < flat.length; i++)
        {
            int row = i / FlopCase.VALUES.length;
            int col = i % FlopCase.VALUES.length;

            offsets[ row ][ col ] = flat[ i ];
        }

        return offsets;
    }

    private static void storeOffsets(int offsets[][])
    {
        LOG.info("storing offsets");
        int flat[] = new int[ offsets.length * offsets[0].length ];

        int index = 0;
        for (int[] subOffsets : offsets)
        {
            for (int offset : subOffsets)
            {
                flat[ index++ ] = offset;
            }
        }
        PersistentInts.persist(flat, OFFSET_FILE);
    }


    //--------------------------------------------------------------------
    private static int[][] calculateOffsets()
    {
        LOG.info("calculating offsets");
        int offsets[][] = new int[ CanonHole.CANONS ][];

        int  offset  = 0;
        Card cards[] = Card.values();
//        for (Card holeCards[] : new Combiner<Card>(Card.VALUES, 2))
//        {
//            CanonHole hole = HoleLookup.lookup(
//                            holeCards[0], holeCards[1]);
        for (int canonHole = 0;
                 canonHole < CanonHole.CANONS;
                 canonHole++)
        {
            CanonHole hole = HoleLookup.lookup( canonHole );
            Card holeCards[] = {hole.a(), hole.b()};

            int subOffsets[] = offsets[ hole.canonIndex() ];
            if (subOffsets != null) continue;
            subOffsets = new int[ FlopCase.values().length ];
            Arrays.fill(subOffsets, -1);

            offsets[ hole.canonIndex() ] = subOffsets;

            Arrs.swap(cards, holeCards[1].ordinal(), 51  );
            Arrs.swap(cards, holeCards[0].ordinal(), 51-1);

            for (Card[] flopCards : new Combiner<Card>(cards, 50, 3))
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

            Arrs.swap(cards, holeCards[0].ordinal(), 51-1);
            Arrs.swap(cards, holeCards[1].ordinal(), 51);
        }

        return offsets;
    }


    //--------------------------------------------------------------------
    public static int globalOffset(CanonHole hole, FlopCase flopCase)
    {
        return OFFSETS[ hole.canonIndex()  ]
                      [ flopCase.ordinal() ];
    }
}
