package ao.bucket.index.incremental;

import ao.bucket.index.iso_flop.IsoFlop;
import ao.bucket.index.iso_turn.IsoTurn;
import ao.bucket.index.iso_turn.TurnCase;
import ao.bucket.index.iso_turn.TurnCaseSet;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;
import ao.util.data.Arr;
import static ao.util.data.Arr.swap;
import ao.util.persist.PersistentBytes;
import ao.util.stats.FastIntCombiner;
import ao.util.stats.FastIntCombiner.CombinationVisitor2;
import ao.util.stats.FastIntCombiner.CombinationVisitor3;

import java.util.BitSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Date: Aug 29, 2008
 * Time: 11:34:21 PM
 */
public class TurnIndexer
{
    //--------------------------------------------------------------------
    private static final String      CASE_FILE =
                                        "lookup/turn_cases.cache";
    
    private static final FlopIndexer FLOPS     = new FlopIndexer();
    private static final int         INDEXES[];
    private static final TurnCaseSet CASE_SETS[];
    private static final int         GLOBAL_OFFSET[];

    static
    {
        INDEXES       = Arr.sequence(Card.VALUES.length);
        CASE_SETS     = retrieveOrCalculateCaseSets();
        GLOBAL_OFFSET = initGlopbalOffsets();
    }


    //--------------------------------------------------------------------
    private static TurnCaseSet[] retrieveOrCalculateCaseSets()
    {
        TurnCaseSet[] caseSets = retrieveCaseSets();
        if (caseSets == null)
        {
            caseSets = calculateCaseSets();
            storeCaseSets(caseSets);
        }
        return caseSets;
    }

    private static TurnCaseSet[] retrieveCaseSets()
    {
        byte asBytes[] = PersistentBytes.retrieve(CASE_FILE);
        if (asBytes == null) return null;

        TurnCaseSet caseSets[] = new TurnCaseSet[ asBytes.length ];
        for (int i = 0; i < asBytes.length; i++)
        {
            caseSets[ i ] = TurnCaseSet.VALUES[ asBytes[i] ];
        }
        return caseSets;
    }

    private static void storeCaseSets(TurnCaseSet caseSets[])
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
    private static TurnCaseSet[] calculateCaseSets()
    {
        final TurnCaseSet caseSets[] =
                new TurnCaseSet[ FlopIndexer.ISO_FLOP_COUNT ];

        final Card   cards[]   = Card.values();
        final BitSet seenHoles = new BitSet();
        new FastIntCombiner(INDEXES, INDEXES.length).combine(
                new CombinationVisitor2() {
            public void visit(int holeA, int holeB)
            {
                Hole hole = Hole.valueOf(
                        cards[holeA], cards[holeB]);

                if (seenHoles.get( hole.suitIsomorphicIndex() )) return;
                seenHoles.set( hole.suitIsomorphicIndex() );
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
            final Hole        hole,
            final Card        cards[],
            final TurnCaseSet caseSets[])
    {
        final BitSet seenFlops = new BitSet();

        new FastIntCombiner(INDEXES, INDEXES.length - 2).combine(
                new CombinationVisitor3() {
            public void visit(int flopA, int flopB, int flopC)
            {
                Card flopCards[] =
                        {cards[flopA], cards[flopB], cards[flopC]};
                IsoFlop isoFlop = hole.isoFlop( flopCards );
                int flopIndex = FLOPS.indexOf(hole, isoFlop);
                if (seenFlops.get( flopIndex )) return;
                seenFlops.set( flopIndex );

                swap(cards, flopC, 51-2);
                swap(cards, flopB, 51-3);
                swap(cards, flopA, 51-4);

                caseSets[ flopIndex ] =
                        iterateTurns(hole, isoFlop, cards, flopCards);

                swap(cards, flopA, 51-4);
                swap(cards, flopB, 51-3);
                swap(cards, flopC, 51-2);
            }});
    }

    private static TurnCaseSet iterateTurns(
            Hole    hole, IsoFlop isoFlop,
            Card    cards[],
            Card    flop[])
    {
        Set<TurnCase> turnCaseBuffer = new LinkedHashSet<TurnCase>();
        for (int turnCardIndex = 0;
                 turnCardIndex < 52 - 2 - 3;
                 turnCardIndex++)
        {
            Card turnCard = cards[ turnCardIndex ];
            IsoTurn isoTurn =
                    isoFlop.isoTurn(
                            hole.asArray(), flop, turnCard);
            turnCaseBuffer.add( isoTurn.subCase() );
        }
        TurnCaseSet caseSet = TurnCaseSet.valueOf(turnCaseBuffer);
        if (caseSet == null)
        {
//            TurnCaseSet.valueOf(turnCaseBuffer);
            System.out.println(
                    "missing TurnCaseSet\t" + turnCaseBuffer);
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
    public int indexOf(Hole hole,
                       Card flop[], IsoFlop isoFlop, int flopIsoIndex,
                       Card turn)
    {
        IsoTurn isoTurn =
                isoFlop.isoTurn(
                        hole.asArray(), flop, turn);

        TurnCase    turnCase = isoTurn.subCase();
        TurnCaseSet caseSet  = CASE_SETS[ flopIsoIndex ];

        int turnCaseOffset = caseSet.offset(turnCase);
        int casedSubIndex  = isoTurn.casedSubIndex(caseSet);
        int localIndex     = turnCaseOffset + casedSubIndex;

        return localIndex + GLOBAL_OFFSET[ flopIsoIndex ];
//        return localIndex;
    }
}
