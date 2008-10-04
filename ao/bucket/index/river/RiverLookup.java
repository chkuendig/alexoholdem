package ao.bucket.index.river;

import ao.bucket.index.flop.Flop;
import ao.bucket.index.test.Gapper;
import ao.bucket.index.turn.Turn;
import ao.bucket.index.turn.TurnLookup;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;
import static ao.util.data.Arr.swap;
import ao.util.persist.PersistentBytes;
import ao.util.stats.FastIntCombiner;
import ao.util.stats.FastIntCombiner.CombinationVisitor2;
import ao.util.stats.FastIntCombiner.CombinationVisitor3;

import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;

/**
 * Date: Sep 4, 2008
 * Time: 12:40:23 PM
 */
public class RiverLookup
{
    //--------------------------------------------------------------------
    private static final String DIR = "lookup/canon/";
    private static final String F_RAW_CASES =
                                    DIR + "river.cases.raw.cache";

    private static final byte   CASES[]   = rawCases();
    private static final int    OFFSETS[] = null;//computeOffsets();


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
//        rawCases();
//        compressRawCases();
        //computeEncoding();

        long size = 0;
        for (byte caseOrdinal : CASES)
        {
            size += RiverCaseSet.VALUES[ CASES[ caseOrdinal ] ].size();
        }
        System.out.println(size);
    }

    //--------------------------------------------------------------------
//    public long indexOf(Hole hole,
//                        Card flop[],
//                        Card turn, IsoTurn isoTurn, int turnIsoIndex,
//                        Card river)
//    {
//        CanonRiver isoRiver = isoTurn.isoRiver(
//                hole.asArray(), flop, turn, river);
//
//        long         encoding       = encodingFor(turnIsoIndex);
//        CaseCount    caseCount      = Encoder.caseCount(encoding);
//        PostFlopCaseSet caseSet        = caseCount.caseSet();
//        long         globalOffset   = Encoder.globalOffset(encoding);
//        int          startTurnIndex = Encoder.turnIndex( encoding );
//
//        int turnIndexDelta  = turnIsoIndex - startTurnIndex;
//        int turnDeltaOffset = turnIndexDelta * caseSet.size();
//        int localIndex      = caseSet.offset( isoRiver.riverCase() ) +
//                              isoRiver.localSubIndex();
//
//        return localIndex;
////        return localIndex + turnDeltaOffset + globalOffset;
//    }
//

    //--------------------------------------------------------------------
    public static RiverCaseSet caseSet(int canonTurn)
    {
        return RiverCaseSet.VALUES[ CASES[canonTurn] ];
    }


    //--------------------------------------------------------------------
    public static long offset(int canonTurn)
    {
        return RiverUtil.unsigned(OFFSETS[ canonTurn ]);
    }
    private static int[] computeOffsets()
    {
        int offset    = 0;
        int offsets[] = new int[ TurnLookup.CANON_TURN_COUNT ];

        for (int i = 0; i < TurnLookup.CANON_TURN_COUNT; i++)
        {
            offsets[ i ] = offset;
            RiverCaseSet caseSet =
                    RiverCaseSet.VALUES[ CASES[ i ] ];
            offset += caseSet.size();
        }

        return offsets;
    }


    //--------------------------------------------------------------------
    private static byte[] rawCases()
    {
        System.out.println("RiverLookup.rawCases");
        byte[] rawCases = PersistentBytes.retrieve(F_RAW_CASES);
        if (rawCases == null)
        {
            rawCases = computeRawCases();
            PersistentBytes.persist(rawCases, F_RAW_CASES);
        }
        return rawCases;
    }
    private static byte[] computeRawCases()
    {
        final Card    cards[]      = Card.values();
        final Gapper  seenTurns    = new Gapper();
        final byte    riverCases[] =
                new byte[ TurnLookup.CANON_TURN_COUNT ];

        final BitSet seenHoles = new BitSet();
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length).combine(
                new CombinationVisitor2() {
            private long prevTime = System.currentTimeMillis();
            public void visit(int holeA, int holeB)
            {
                Hole hole = Hole.valueOf(
                        cards[holeA], cards[holeB]);
//                if (hole.suited()) return;

                if (seenHoles.get( hole.canonIndex() )) return;
                seenHoles.set( hole.canonIndex() );

                swap(cards, holeB, 51  );
                swap(cards, holeA, 51-1);

                iterateFlops(hole, cards, seenTurns, riverCases);
                System.out.println(System.currentTimeMillis() - prevTime);
                prevTime = System.currentTimeMillis();

                swap(cards, holeA, 51-1);
                swap(cards, holeB, 51  );
            }
        });

        return riverCases;
    }

    private static void iterateFlops(
            final Hole    hole,
            final Card    cards[],
            final Gapper  seenTurns,
            final byte    riverCases[])
    {
        final BitSet seenFlops = new BitSet();

        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length - 2)
                .combine(new CombinationVisitor3() {
            public void visit(int flopA, int flopB, int flopC)
            {
                Flop flop = hole.addFlop(
                        cards[flopA], cards[flopB], cards[flopC]);

                int flopIndex = flop.canonIndex();
                if (seenFlops.get( flopIndex )) return;
                seenFlops.set( flopIndex );

                swap(cards, flopC, 51-2);
                swap(cards, flopB, 51-3);
                swap(cards, flopA, 51-4);

                iterateTurns(
                        flop, cards, seenTurns, riverCases);

                swap(cards, flopA, 51-4);
                swap(cards, flopB, 51-3);
                swap(cards, flopC, 51-2);
            }});

        System.out.println(hole);
    }

    private static void iterateTurns(
            Flop   flop,
            Card   cards[],
            Gapper seenTurns,
            byte   riverCases[])
    {
        for (int turnCardIndex = 0;
                 turnCardIndex < 51 - 4;
                 turnCardIndex++)
        {
            Card turnCard = cards[ turnCardIndex ];
            Turn turn     = flop.addTurn(turnCard);

            int turnIndex = turn.canonIndex();
            if (seenTurns.get( turnIndex )) continue;
            seenTurns.set( turnIndex );

            swap(cards, turnCardIndex, 51-5);
            riverCases[ turnIndex ] = (byte)
                    iterateRivers(turn, cards).ordinal();
            swap(cards, turnCardIndex, 51-5);
        }
    }

    private static RiverCaseSet iterateRivers(
            Turn turn, Card cards[])
    {
        Set<RiverCase> caseBuffer = EnumSet.noneOf( RiverCase.class );
//        EnumMap<RiverCase, int[]> caseBuffer =
//                new EnumMap<RiverCase, int[]>( RiverCase.class );
        for (int riverCardIndex = 0;
                 riverCardIndex < 51 - 5;
                 riverCardIndex++)
        {
            Card       riverCard = cards[ riverCardIndex ];
            River river     = turn.addRiver(riverCard);
            RiverCase  riverCase = river.riverCase();

//            if (riverCase != RiverCase.T0) continue;
//            System.out.println(
//                    turn + "\t" +
//                    riverCard + "\t" + riverCase);

            caseBuffer.add( riverCase );
//            int count[] = caseBuffer.get( riverCase );
//            if (count == null)
//            {
//                count = new int[]{ 1 };
//                caseBuffer.put( riverCase, count );
//            }
//            else
//            {
//                count[0]++;
//            }
        }

//        for (Map.Entry<RiverCase, int[]> e : caseBuffer.entrySet())
//        {
//            if (e.getKey().size() != e.getValue()[0])
//            {
//                System.out.println(
//                        "MISMATCH: " + turn + "\t" +
//                        e.getKey() + "\t" + e.getValue()[0]);
//            }
//        }
//
        return RiverCaseSet.valueOf( caseBuffer );
//        return RiverCaseSet.valueOf(caseBuffer.keySet());
    }
}
