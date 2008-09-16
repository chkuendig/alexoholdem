package ao.bucket.index.post_flop.river;

import ao.bucket.index.card.CanonSuit;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.post_flop.common.CanonSuitSet;
import ao.bucket.index.post_flop.turn.Turn;
import ao.bucket.index.test.Gapper;
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
    private static final String F_ZIP_CASES =
                                    DIR + "river.cases.zip.cache";

//    public static final long CODED_OFFSETS[] = computeEncoding();

//    private static final int COMPRESSED_SIZE =  3452841;
    private static final int        RAW_SIZE = 51520872;


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        computeRawCases();
        //compressRawCases();
        //computeEncoding();

//        long size = 0;
//        for (long encoding : CODED_TURNS)
//        {
//            size += Encoder.caseCount(encoding)
//                           .totalSize();
//        }
//        System.out.println(size);
//        System.out.println((int)size);
    }


    //--------------------------------------------------------------------
    public static CanonSuitSet riverCaseSet(int forTurnIndex)
    {
//        return Encoder.caseCount(
//                   encodingFor(forTurnIndex)
//               ).caseSet();
        return null;
    }

//    public static PostFlopCaseSet readRiverCaseSet(int forTurnIndex)
//    {
//        RandomAccessFile f = null;
//        try
//        {
//            f = new RandomAccessFile(RAW_RIVER_CASES, "r");
//            f.seek(forTurnIndex);
//            byte riverCaseSetOrdinal = f.readByte();
//            return PostFlopCaseSet.VALUES[
//                    riverCaseSetOrdinal ];
//        }
//        catch (IOException e)
//        {
//            throw new Error( e );
//        }
//        finally
//        {
//            if (f != null)
//            {
//                try
//                {
//                    f.close();
//                }
//                catch (IOException e)
//                {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


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
////    private static long encodingFor(int turnIsoIndex)
////    {
////        int index = 0;
////        for (long encoding : CODED_TURNS)
////        {
////            index += Encoder.caseCount(encoding).count();
////
////            if (index > turnIsoIndex)
//////            if ((Encoder.turnIndex(encoding) +
//////                 Encoder.caseCount(encoding).count() - 1)
//////                    >= turnIsoIndex)
////            {
////                return encoding;
////            }
////        }
////        return -1;
////    }
//    private static long encodingFor(int turnIsoIndex)
//    {
//        int lo = 0;
//        int hi = CODED_TURNS.length - 1;
//
//        while (lo <= hi)
//        {
//            int mid = (lo + hi) / 2;
//
//            int turnIndex = Encoder.turnIndex( CODED_TURNS[mid] );
//            if (turnIndex > turnIsoIndex)
//            {
//                hi = mid - 1;
//            }
//            else
//            {
//                CaseCount caseCount =
//                        Encoder.caseCount( CODED_TURNS[mid] );
//                if ((turnIndex + caseCount.count() - 1) < turnIsoIndex)
//                {
//                    lo = mid + 1;
//                }
//                else
//                {
//                    return CODED_TURNS[mid];
//                }
//            }
//        }
//        return -1;
//    }
//
//
//    //--------------------------------------------------------------------
//    private static long[] computeEncoding()
//    {
//        byte zipRivers[] = compressedCases();
//        long encoded[]   = new long[ COMPRESSED_SIZE ];
//
//        int  index        = 0;
//        int  flopIndex    = 0;
//        long globalOffset = 0;
//        for (byte zipRiver : zipRivers)
//        {
//            CaseCount cc = CaseCount.VALUES[ zipRiver ];
//
////            if ((flopIndex + cc.count()) >= 28818)
////            {
////                System.out.println(index);
////            }
//
//            encoded[ index++ ] = Encoder.encode(
//                    flopIndex, globalOffset, cc);
//            flopIndex    += cc.count();
//            globalOffset += cc.totalSize();
//        }
//
//        return encoded;
//    }
//
//
//    //--------------------------------------------------------------------
//    private static byte[] compressedCases()
//    {
//        byte zipRivers[] = PersistentBytes.retrieve(ZIP_RIVER_CASES);
//        if (zipRivers == null)
//        {
//            zipRivers = compressRawCases();
//            PersistentBytes.persist(zipRivers, ZIP_RIVER_CASES);
//
//            testCompression(zipRivers, rawCases());
//        }
//        return zipRivers;
//    }
//    private static byte[] compressRawCases()
//    {
//        byte riverCases[] = rawCases();
//        byte zipRivers[]  = new byte[ COMPRESSED_SIZE ];
//
//        int  groups =  0;
//        int  count  =  0;
//        byte prev   = -1;
//        for (byte riverCase : riverCases)
//        {
//            if (riverCase != prev)
//            {
//                if (count != 0)
//                {
//                    CaseCount caseCount = CaseCount.valueOf(
//                            PostFlopCaseSet.VALUES[ prev ],
//                            Count.valueOf( count ));
//                    zipRivers[ groups++ ] = (byte) caseCount.ordinal();
//                }
//                count = 1;
//                prev  = riverCase;
//            }
//            else
//            {
//                count++;
//            }
//        }
//        if (count != 0)
//        {
//            CaseCount caseCount = CaseCount.valueOf(
//                    PostFlopCaseSet.VALUES[ prev ],
//                    Count.valueOf( count ));
//            zipRivers[ groups ] = (byte) caseCount.ordinal();
//        }
//
//        return zipRivers;
//    }
//    private static void testCompression(
//            byte compressed[],
//            byte raw[])
//    {
//        int index = 0;
//        for (byte zipCase : compressed)
//        {
//            CaseCount caseCount = CaseCount.VALUES[ zipCase ];
//
//            for (int i = 0; i < caseCount.count(); i++)
//            {
//                PostFlopCaseSet caseSet =
//                    PostFlopCaseSet.VALUES[ raw[index++] ];
//                if (caseCount.caseSet() != caseSet)
//                {
//                    System.out.println(
//                            caseCount.caseSet() + "\t" + caseSet);
//                }
//            }
//        }
//    }
//
//
//    //--------------------------------------------------------------------
    private static byte[] rawCases()
    {
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
        final byte    riverCases[] = new byte[ RAW_SIZE ];

        final BitSet seenHoles  = new BitSet();
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length).combine(
                new CombinationVisitor2() {
            private long prevTime = System.currentTimeMillis();
            public void visit(int holeA, int holeB)
            {
                Hole hole = Hole.valueOf(
                        cards[holeA], cards[holeB]);

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
                 turnCardIndex < 52 - 2 - 3;
                 turnCardIndex++)
        {
            Card turnCard = cards[ turnCardIndex ];
            Turn turn     = flop.addTurn(turnCard);

            int turnIndex = turn.canonIndex();
            if (seenTurns.get( turnIndex )) continue;
            seenTurns.set( turnIndex );

            swap(cards, turnCardIndex, 51-5);
            iterateRivers(turn, cards, turnIndex, riverCases);
            swap(cards, turnCardIndex, 51-5);
        }
    }

    private static void iterateRivers(
            Turn  turn,
            Card  cards[],
            int   turnIndex,
            byte  riverCases[])
    {
        Set<CanonSuit> caseBuffer = EnumSet.noneOf( CanonSuit.class );
        for (int riverCardIndex = 0;
                 riverCardIndex < 52 - 2 - 3 - 1;
                 riverCardIndex++)
        {
            Card       riverCard = cards[ riverCardIndex ];
            CanonRiver river     = turn.addRiver(riverCard);

            caseBuffer.add( river.riverSuit() );
        }
        riverCases[ turnIndex ] = (byte)
                CanonSuitSet.valueOf(caseBuffer).ordinal();
    }
}
