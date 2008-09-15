package ao.bucket.index.post_flop.turn;

import ao.bucket.index.flop.Flop;
import ao.bucket.index.flop.FlopOffset;
import ao.bucket.index.post_flop.common.PostFlopCase;
import ao.bucket.index.post_flop.common.PostFlopCaseSet;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;
import static ao.util.data.Arr.swap;
import ao.util.persist.PersistentBytes;
import ao.util.stats.FastIntCombiner;
import ao.util.stats.FastIntCombiner.CombinationVisitor2;
import ao.util.stats.FastIntCombiner.CombinationVisitor3;

import java.util.BitSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Date: Sep 14, 2008
 * Time: 9:50:10 PM
 */
public class TurnLookup
{
    //--------------------------------------------------------------------
    private static final String          CASE_FILE =
                                             "lookup/new_turn_cases.cache";
    private static final PostFlopCaseSet CASE_SETS[];
    private static final int             GLOBAL_OFFSET[];

    static
    {
        CASE_SETS     = retrieveOrCalculateCaseSets();
        GLOBAL_OFFSET = initGlopbalOffsets();
    }


    //--------------------------------------------------------------------
    private static PostFlopCaseSet[] retrieveOrCalculateCaseSets()
    {
        PostFlopCaseSet[] caseSets = retrieveCaseSets();
        if (caseSets == null)
        {
            caseSets = calculateCaseSets();
            storeCaseSets(caseSets);
        }
        return caseSets;
    }

    private static PostFlopCaseSet[] retrieveCaseSets()
    {
        byte asBytes[] = PersistentBytes.retrieve(CASE_FILE);
        if (asBytes == null) return null;

        PostFlopCaseSet caseSets[] =
                new PostFlopCaseSet[ asBytes.length ];
        for (int i = 0; i < asBytes.length; i++)
        {
            caseSets[ i ] = PostFlopCaseSet.VALUES[ asBytes[i] ];
        }
        return caseSets;
    }

    private static void storeCaseSets(PostFlopCaseSet caseSets[])
    {
        byte asBytes[] = new byte[ caseSets.length ];
        for (int i = 0; i < caseSets.length; i++)
        {
            if (caseSets[ i ] == null)
            {
                System.out.println("MISSING\t" + i);
                asBytes[ i ] = -1;
            }
            else
            {
                asBytes[ i ] = (byte) caseSets[ i ].ordinal();
            }
        }
        PersistentBytes.persist(asBytes, CASE_FILE);
    }


    //--------------------------------------------------------------------
    private static PostFlopCaseSet[] calculateCaseSets()
    {
        final PostFlopCaseSet caseSets[] =
                new PostFlopCaseSet[ FlopOffset.ISO_FLOP_COUNT ];

        final Card   cards[]   = Card.values();
        final BitSet seenHoles = new BitSet();
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length).combine(
                new CombinationVisitor2() {
            public void visit(int holeA, int holeB)
            {
                Hole hole = Hole.valueOf(
                        cards[holeA], cards[holeB]);

                if (seenHoles.get( hole.canonIndex() )) return;
                seenHoles.set( hole.canonIndex() );
                System.out.println("calculating\t" + hole);

                swap(cards, holeB, 51  );
                swap(cards, holeA, 51-1);

                iterateFlops(hole, cards, caseSets);

                swap(cards, holeA, 51-1);
                swap(cards, holeB, 51  );
            }
        });

        return caseSets;
    }

    private static void iterateFlops(
            final Hole            hole,
            final Card            cards[],
            final PostFlopCaseSet caseSets[])
    {
        final BitSet seenFlops = new BitSet();

        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length - 2)
                .combine(new CombinationVisitor3() {
            public void visit(int flopA, int flopB, int flopC)
            {
                Card flopCards[] =
                        {cards[flopA], cards[flopB], cards[flopC]};
                Flop isoFlop = hole.isoFlop(
                                    flopCards[0],
                                    flopCards[1],
                                    flopCards[2]);
                int flopIndex = isoFlop.canonIndex();
//                        FLOPS.indexOf(hole, isoFlop);
                if (seenFlops.get( flopIndex )) return;
                seenFlops.set( flopIndex );

                swap(cards, flopC, 51-2);
                swap(cards, flopB, 51-3);
                swap(cards, flopA, 51-4);

                caseSets[ flopIndex ] = iterateTurns(isoFlop, cards);

                swap(cards, flopA, 51-4);
                swap(cards, flopB, 51-3);
                swap(cards, flopC, 51-2);
            }});
    }

    private static PostFlopCaseSet iterateTurns(
            Flop isoFlop,
            Card cards[])
    {
        Set<PostFlopCase> turnCaseBuffer = new LinkedHashSet<PostFlopCase>();
        for (int turnCardIndex = 0;
                 turnCardIndex < 52 - 2 - 3;
                 turnCardIndex++)
        {
            Card    turnCard = cards[ turnCardIndex ];
            Turn isoTurn  = isoFlop.isoTurn(turnCard);
            turnCaseBuffer.add( isoTurn.turnCase() );
        }
        PostFlopCaseSet caseSet = PostFlopCaseSet.valueOf(turnCaseBuffer);
        if (caseSet == null)
        {
//            PostFlopCaseSet.valueOf(turnCaseBuffer);
            System.out.println(
                    "missing PostFlopCaseSet\t" + turnCaseBuffer);
        }
        return caseSet;
    }


    //--------------------------------------------------------------------
    private static int[] initGlopbalOffsets()
    {
        int   offset        = 0;
        int[] globalOffsets = new int[ CASE_SETS.length ];
        for (int i = 0; i < CASE_SETS.length; i++)
        {
            globalOffsets[ i ] = offset;
            offset += CASE_SETS[ i ].size();
        }
        return globalOffsets;
    }


    //--------------------------------------------------------------------
    public static PostFlopCaseSet caseSet(int flopIndex)
    {
        return CASE_SETS[ flopIndex ];
    }

    public static int globalOffset(int flopIndex)
    {
        return GLOBAL_OFFSET[ flopIndex ];
    }


//    public int indexOf(Hole hole,
//                       Card flop[], Flop isoFlop, int flopIsoIndex,
//                       Card turn)
//    {
////        IsoTurn isoTurn =
////                isoFlop.isoTurn(
////                        hole.asArray(), flop, turn);
////        return indexOf(flopIsoIndex, isoTurn);
//        return -1;
//    }
//    public int indexOf(int     flopIsoIndex,
//                       IsoTurn flop)
//    {
////        PostFlopCase turnCase = isoTurn.turnCase();
////        return turnCase.subIndex();
//        return flop.localSubIndex();
//
////        TurnCaseSet caseSet  = CASE_SETS[ flopIsoIndex ];
////
////        int turnCaseOffset = caseSet.offset(turnCase);
////        int casedSubIndex  = isoTurn.casedSubIndex(caseSet);
////        int localIndex     = turnCaseOffset + casedSubIndex;
////
////        return localIndex + GLOBAL_OFFSET[ flopIsoIndex ];
//////        return localIndex;
//    }
}
