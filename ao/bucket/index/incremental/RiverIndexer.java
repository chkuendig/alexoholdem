package ao.bucket.index.incremental;

import ao.bucket.index.Indexer;
import ao.bucket.index.iso_flop.IsoFlop;
import ao.bucket.index.iso_river.IsoRiver;
import ao.bucket.index.iso_river.RiverCase;
import ao.bucket.index.iso_river.RiverCaseSet;
import ao.bucket.index.iso_river.encode.CaseCount;
import ao.bucket.index.iso_river.encode.Count;
import ao.bucket.index.iso_river.encode.Encoder;
import ao.bucket.index.iso_turn.IsoTurn;
import ao.bucket.index.test.Gapper;
import ao.holdem.model.card.Card;
import static ao.holdem.model.card.Card.INDEXES;
import static ao.holdem.model.card.Card.values;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;
import ao.holdem.model.card.sequence.LiteralCardSequence;
import static ao.util.data.Arr.swap;
import ao.util.persist.PersistentBytes;
import ao.util.stats.FastIntCombiner;
import ao.util.stats.FastIntCombiner.CombinationVisitor2;
import ao.util.stats.FastIntCombiner.CombinationVisitor3;

import java.util.BitSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Date: Sep 4, 2008
 * Time: 12:40:23 PM
 */
public class RiverIndexer
{
    //--------------------------------------------------------------------
    private static final String RAW_RIVER_CASES =
                                    "lookup/raw_river_cases.cache";
    private static final String ZIP_RIVER_CASES =
                                    "lookup/zip_river_cases.cache";

    public static final long CODED_TURNS[] = computeEncoding();

    private static final int COMPRESSED_SIZE =  3452841;
    private static final int        RAW_SIZE = 51520872;


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        //computeRawCases();
        //compressRawCases();
        //computeEncoding();

        long size = 0;
        for (long encoding : CODED_TURNS)
        {
            size += Encoder.caseCount(encoding)
                           .totalSize();
        }
        System.out.println(size);
        System.out.println((int)size);
    }


    //--------------------------------------------------------------------
    public static RiverCaseSet riverCaseSet(int forTurnIndex)
    {
        return Encoder.caseCount(
                   encodingFor(forTurnIndex)
               ).caseSet();
    }

//    public static RiverCaseSet readRiverCaseSet(int forTurnIndex)
//    {
//        RandomAccessFile f = null;
//        try
//        {
//            f = new RandomAccessFile(RAW_RIVER_CASES, "r");
//            f.seek(forTurnIndex);
//            byte riverCaseSetOrdinal = f.readByte();
//            return RiverCaseSet.VALUES[
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
    public long indexOf(Hole hole,
                        Card flop[],
                        Card turn, IsoTurn isoTurn, int turnIsoIndex,
                        Card river)
    {
        IsoRiver isoRiver = isoTurn.isoRiver(
                hole.asArray(), flop, turn, river);

        long         encoding       = encodingFor(turnIsoIndex);
        CaseCount    caseCount      = Encoder.caseCount(encoding);
        RiverCaseSet caseSet        = caseCount.caseSet();
        long         globalOffset   = Encoder.globalOffset(encoding);
        int          startTurnIndex = Encoder.turnIndex( encoding );

        int turnIndexDelta  = turnIsoIndex - startTurnIndex;
        int turnDeltaOffset = turnIndexDelta * caseSet.size();
        int localIndex      = caseSet.offset( isoRiver.riverCase() ) +
                              isoRiver.localSubIndex();

        return localIndex;
//        return localIndex + turnDeltaOffset + globalOffset;
    }

//    private static long encodingFor(int turnIsoIndex)
//    {
//        int index = 0;
//        for (long encoding : CODED_TURNS)
//        {
//            index += Encoder.caseCount(encoding).count();
//
//            if (index > turnIsoIndex)
////            if ((Encoder.turnIndex(encoding) +
////                 Encoder.caseCount(encoding).count() - 1)
////                    >= turnIsoIndex)
//            {
//                return encoding;
//            }
//        }
//        return -1;
//    }
    private static long encodingFor(int turnIsoIndex)
    {
        int lo = 0;
        int hi = CODED_TURNS.length - 1;

        while (lo <= hi)
        {
            int mid = (lo + hi) / 2;

            int turnIndex = Encoder.turnIndex( CODED_TURNS[mid] );
            if (turnIndex > turnIsoIndex)
            {
                hi = mid - 1;
            }
            else
            {
                CaseCount caseCount =
                        Encoder.caseCount( CODED_TURNS[mid] );
                if ((turnIndex + caseCount.count() - 1) < turnIsoIndex)
                {
                    lo = mid + 1;
                }
                else
                {
                    return CODED_TURNS[mid];
                }
            }
        }
        return -1;
    }


    //--------------------------------------------------------------------
    private static long[] computeEncoding()
    {
        byte zipRivers[] = compressedCases();
        long encoded[]   = new long[ COMPRESSED_SIZE ];

        int  index        = 0;
        int  flopIndex    = 0;
        long globalOffset = 0;
        for (byte zipRiver : zipRivers)
        {
            CaseCount cc = CaseCount.VALUES[ zipRiver ];

//            if ((flopIndex + cc.count()) >= 28818)
//            {
//                System.out.println(index);
//            }

            encoded[ index++ ] = Encoder.encode(
                    flopIndex, globalOffset, cc);
            flopIndex    += cc.count();
            globalOffset += cc.totalSize();
        }

        return encoded;
    }


    //--------------------------------------------------------------------
    private static byte[] compressedCases()
    {
        byte zipRivers[] = PersistentBytes.retrieve(ZIP_RIVER_CASES);
        if (zipRivers == null)
        {
            zipRivers = compressRawCases();
            PersistentBytes.persist(zipRivers, ZIP_RIVER_CASES);

            testCompression(zipRivers, rawCases());
        }
        return zipRivers;
    }
    private static byte[] compressRawCases()
    {
        byte riverCases[] = rawCases();
        byte zipRivers[]  = new byte[ COMPRESSED_SIZE ];

        int  groups =  0;
        int  count  =  0;
        byte prev   = -1;
        for (byte riverCase : riverCases)
        {
            if (riverCase != prev)
            {
                if (count != 0)
                {
                    CaseCount caseCount = CaseCount.valueOf(
                            RiverCaseSet.VALUES[ prev ],
                            Count.valueOf( count ));
                    zipRivers[ groups++ ] = (byte) caseCount.ordinal();
                }
                count = 1;
                prev  = riverCase;
            }
            else
            {
                count++;
            }
        }
        if (count != 0)
        {
            CaseCount caseCount = CaseCount.valueOf(
                    RiverCaseSet.VALUES[ prev ],
                    Count.valueOf( count ));
            zipRivers[ groups ] = (byte) caseCount.ordinal();
        }

        return zipRivers;
    }
    private static void testCompression(
            byte compressed[],
            byte raw[])
    {
        int index = 0;
        for (byte zipCase : compressed)
        {
            CaseCount caseCount = CaseCount.VALUES[ zipCase ];

            for (int i = 0; i < caseCount.count(); i++)
            {
                RiverCaseSet caseSet =
                    RiverCaseSet.VALUES[ raw[index++] ];
                if (caseCount.caseSet() != caseSet)
                {
                    System.out.println(
                            caseCount.caseSet() + "\t" + caseSet);
                }
            }
        }
    }

    
    //--------------------------------------------------------------------
    private static byte[] rawCases()
    {
        byte[] rawCases = PersistentBytes.retrieve(RAW_RIVER_CASES);
        if (rawCases == null)
        {
            rawCases = computeRawCases();
            PersistentBytes.persist(rawCases, RAW_RIVER_CASES);
        }
        return rawCases;
    }
    private static byte[] computeRawCases()
    {
        final Card    cards[]      = values();
        final Indexer indexer      = new IndexerImpl();
        final Gapper  seenTurns    = new Gapper();
        final byte    riverCases[] = new byte[ RAW_SIZE ];

        final BitSet seenHoles  = new BitSet();
        new FastIntCombiner(INDEXES, INDEXES.length).combine(
                new CombinationVisitor2() {
            private long prevTime = System.currentTimeMillis();
            public void visit(int holeA, int holeB)
            {
                Hole hole = Hole.valueOf(
                        cards[holeA], cards[holeB]);

                if (seenHoles.get( hole.suitIsomorphicIndex() )) return;
                seenHoles.set( hole.suitIsomorphicIndex() );

                swap(cards, holeB, 51  );
                swap(cards, holeA, 51-1);

                iterateFlops(hole, cards, seenTurns, indexer, riverCases);
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
            final Indexer indexer,
            final byte    riverCases[])
    {
        final BitSet seenFlops = new BitSet();

        new FastIntCombiner(INDEXES, INDEXES.length - 2).combine(
                new CombinationVisitor3() {
            public void visit(int flopA, int flopB, int flopC)
            {
                Card flopCards[] =
                        {cards[flopA], cards[flopB], cards[flopC]};

                swap(cards, flopC, 51-2);
                swap(cards, flopB, 51-3);
                swap(cards, flopA, 51-4);

                CardSequence cardSeq =
                    new LiteralCardSequence(
                            hole, new Community(
                            flopCards[0], flopCards[1], flopCards[2]));
                int index = (int)indexer.indexOf(cardSeq);

                if (! seenFlops.get( index ))
                {
                    seenFlops.set( index );
                    iterateTurns(
                            cards, hole, flopCards, seenTurns, indexer,
                            riverCases);
                }

                swap(cards, flopA, 51-4);
                swap(cards, flopB, 51-3);
                swap(cards, flopC, 51-2);
            }});

        System.out.println(hole);
    }

    private static void iterateTurns(
            Card    cards[],
            Hole    hole,
            Card    flop[],
            Gapper  seenTurns,
            Indexer indexer,
            byte    riverCases[])
    {
        for (int turnCardIndex = 0;
                 turnCardIndex < 52 - 2 - 3;
                 turnCardIndex++)
        {
            Card turnCard = cards[ turnCardIndex ];

            CardSequence seq = new LiteralCardSequence(hole,
                   new Community(flop[0], flop[1], flop[2], turnCard));
            int turnIndex = (int) indexer.indexOf(seq);

            if (seenTurns.get( turnIndex )) continue;
                seenTurns.set( turnIndex );

            swap(cards, turnCardIndex, 51-5);
            iterateRivers(cards, hole, flop, turnCard, turnIndex,
                          riverCases);
            swap(cards, turnCardIndex, 51-5);
        }
    }

    private static void iterateRivers(
            Card cards[],
            Hole hole,
            Card flop[],
            Card turn,
            int  turnIndex,
            byte riverCases[])
    {
        Set<RiverCase> caseBuffer = new TreeSet<RiverCase>();
        for (int riverCardIndex = 0;
                 riverCardIndex < 52 - 2 - 3 - 1;
                 riverCardIndex++)
        {
            Card riverCard = cards[ riverCardIndex ];

            IsoFlop isoFlop  = hole.isoFlop(flop);
            IsoTurn isoTurn  = isoFlop.isoTurn(
                    hole.asArray(), flop, turn);
            IsoRiver isoRiver = isoTurn.isoRiver(
                    hole.asArray(), flop, turn, riverCard);
            caseBuffer.add( isoRiver.riverCase() );
        }
        riverCases[ turnIndex ] = (byte)
                RiverCaseSet.valueOf(caseBuffer).ordinal();
    }
}
