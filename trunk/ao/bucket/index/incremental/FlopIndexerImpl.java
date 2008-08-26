package ao.bucket.index.incremental;

import ao.bucket.index.Indexer;
import ao.bucket.index.iso_cards.IsoFlop;
import ao.bucket.index.iso_case.FlopCase;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;
import static ao.util.data.Arr.swap;
import ao.util.persist.PersistentInts;
import ao.util.stats.Combiner;

import java.util.Arrays;

/**
 * Date: Aug 16, 2008
 * Time: 2:48:07 PM
 */
public class FlopIndexerImpl implements Indexer
{
    //--------------------------------------------------------------------
    private static final String OFFSET_FILE = "lookup/flop_offsets.cache";
    private static final int OFFSETS[][] = retrieveOrCalculateOffsets();


    private static int[][] retrieveOrCalculateOffsets()
    {
        int offsets[][] = retrieveOffsets();
        if (offsets == null)
        {
            offsets = calculateOffsets();
            storeOffsets( offsets );
        }
        return offsets;
    }

    private static int[][] retrieveOffsets()
    {
        int flat[] = PersistentInts.retrieve(OFFSET_FILE);
        if (flat == null) return null;

        int offsets[][] =
                new int[ Hole.SUIT_ISOMORPHIC_COUNT ]
                       [ FlopCase.VALUES.length     ];

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
        int offsets[][] = new int[ Hole.SUIT_ISOMORPHIC_COUNT ][];

        int  offset  = 0;
        Card cards[] = Card.values();
        for (Card holeCards[] : new Combiner<Card>(Card.VALUES, 2))
        {
            Hole hole = Hole.newInstance(
                            holeCards[0], holeCards[1]);

            int subOffsets[] = offsets[ hole.suitIsomorphicIndex() ];
            if (subOffsets != null) continue;
            subOffsets = new int[ FlopCase.values().length ];
            Arrays.fill(subOffsets, -1);

            offsets[ hole.suitIsomorphicIndex() ] = subOffsets;

            swap(cards, holeCards[1].ordinal(), 51  );
            swap(cards, holeCards[0].ordinal(), 51-1);

            for (Card flopCards[] : new Combiner<Card>(cards, 50, 3))
            {
                IsoFlop isoFlop = hole.isoFlop( flopCards );

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
    public int indexOf(CardSequence cards)
    {
        if (cards.community().isPreflop())
        {
            return cards.hole().suitIsomorphicIndex();
        }

        Hole    hole = cards.hole();
        IsoFlop flop = hole.isoFlop(cards.community().flopA(),
                                    cards.community().flopB(),
                                    cards.community().flopC());

        flop


//        int offset    = 0;
//        int holeIndex = isoHole.holeCase().subIndex();
//
//        FlopSubIndexer subIndexers[] =
//                (isoHole.holeCase().type() == Type.PAIR
//                 ? PostPairFlop.values()
//                 : isoHole.holeCase().type() == Type.SUITED
//                   ? PostSuitedFlop.values()
//                   : PostUnsuitedFlop.values());
//
//        for (FlopSubIndexer subIndexer : subIndexers)
//        {
//            if (subIndexer.caseEquals(flop.flopCase()))
//            {
//                return offset +
//                       subIndexer.subIndex( flop ) *
//                            isoHole.holeCase().type().members() +
//                       holeIndex;
//
////                return subIndexer.subIndex( flop ) *
////                            isoHole.holeCase().type().members() +
////                       holeIndex;
//
////                return subIndexer.subIndex( flop );
//            }
//            offset += subIndexer.size() *
//                        isoHole.holeCase().type().members();
//        }

        return -1;
    }




}