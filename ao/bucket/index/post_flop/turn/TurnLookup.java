package ao.bucket.index.post_flop.turn;

import ao.bucket.index.card.CanonCard;
import ao.bucket.index.card.CanonSuit;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.flop.FlopOffset;
import ao.bucket.index.post_flop.common.CanonSuitSet;
import ao.bucket.index.post_flop.common.Codac;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.Rank;
import ao.holdem.model.card.Suit;
import static ao.util.data.Arr.swap;
import ao.util.persist.PersistentBytes;
import ao.util.stats.FastIntCombiner;
import ao.util.stats.FastIntCombiner.CombinationVisitor2;
import ao.util.stats.FastIntCombiner.CombinationVisitor3;

import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;

/**
 * Date: Sep 14, 2008
 * Time: 9:50:10 PM
 */
public class TurnLookup
{
    //--------------------------------------------------------------------
    private static final String       RAW_CASE_FILE =
                                        "lookup/canon/turn.cases.cache";
    private static final int          CODED_OFFSET[][];
//    private static final CanonSuitSet CASE_SETS[][];
//    private static final int          GLOBAL_OFFSET[][];

    static
    {
        CanonSuitSet caseSets[][] =
                retrieveOrCalculateCaseSets();
        CODED_OFFSET = encodeOffsets(caseSets);
    }

    public static void main(String args[])
    {
        // initialize
    }


    //--------------------------------------------------------------------
    private static CanonSuitSet[][] retrieveOrCalculateCaseSets()
    {
        CanonSuitSet[][] caseSets = retrieveCaseSets();
        if (caseSets == null)
        {
            caseSets = calculateCaseSets();
            storeCaseSets(caseSets);
        }
        return caseSets;
    }

    private static CanonSuitSet[][] retrieveCaseSets()
    {
        byte asBytes[] = PersistentBytes.retrieve(RAW_CASE_FILE);
        if (asBytes == null) return null;

        int          flatIndex    = 0;
        CanonSuitSet caseSets[][] =
                new CanonSuitSet
                        [ asBytes.length / Rank.VALUES.length ]
                        [ Rank.VALUES.length                  ];
        for (int i = 0; i < caseSets.length; i++)
        {
            for (int j = 0; j < Rank.VALUES.length; j++)
            {
                int ordinal = asBytes[flatIndex++];
                if (ordinal >= 0)
                {
                    caseSets[ i ][ j ] =
                            CanonSuitSet.VALUES[ ordinal ];
                }
            }
        }
        return caseSets;
    }

    private static void storeCaseSets(CanonSuitSet caseSets[][])
    {
        int  flatIndex = 0;
        byte asBytes[] = new byte[ caseSets.length * Rank.VALUES.length ];
        for (CanonSuitSet[] caseSet : caseSets)
        {
            for (CanonSuitSet aCaseSet : caseSet)
            {
                asBytes[flatIndex++] = (byte) (
                        (aCaseSet == null)
                        ? -1
                        : aCaseSet.ordinal());
            }
        }
        PersistentBytes.persist(asBytes, RAW_CASE_FILE);
    }


    //--------------------------------------------------------------------
    private static CanonSuitSet[][] calculateCaseSets()
    {
        final CanonSuitSet caseSets[][] =
                new CanonSuitSet[ FlopOffset.ISO_FLOP_COUNT ]
                                [ /*Rank.VALUES.length   */ ];

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
            final Hole         hole,
            final Card         cards[],
            final CanonSuitSet caseSets[][])
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
                if (seenFlops.get( flopIndex )) return;
                seenFlops.set( flopIndex );

                swap(cards, flopC, 51-2);
                swap(cards, flopB, 51-3);
                swap(cards, flopA, 51-4);

                caseSets[ flopIndex ] =
                        iterateTurns(isoFlop, cards);

                swap(cards, flopA, 51-4);
                swap(cards, flopB, 51-3);
                swap(cards, flopC, 51-2);
            }});
    }

    private static CanonSuitSet[] iterateTurns(
            Flop isoFlop,
            Card cards[])
    {
        CanonSuitSet turnCases[] =
                new CanonSuitSet[ Rank.VALUES.length ];

        for (Rank rank : Rank.VALUES)
        {
            Set<CanonSuit> buffer =
                    EnumSet.noneOf( CanonSuit.class );

            suits:
            for (Suit suit : Suit.VALUES)
            {
                Card turnCard = Card.valueOf(rank, suit);
                for (int i = 47; i < 52; i++)
                {
                    if (cards[ i ] == turnCard)
                    {
                        continue suits;
                    }
                }

                Turn turn = isoFlop.isoTurn(turnCard);
                buffer.add( turn.turnSuit() );

//                System.out.println(
//                        cards[49] + "," + cards[48 ]+ "," + cards[47] +
//                        "|" + turnCard + "\t" +
//                        turn.turnCase());
            }

            if (! buffer.isEmpty())
            {
                turnCases[ rank.ordinal() ] =
                    CanonSuitSet.valueOf(buffer);
            }
        }
        return turnCases;
    }


    //--------------------------------------------------------------------
    private static int[][] encodeOffsets(CanonSuitSet caseSets[][])
    {
        int offset           = 0;
        int codedOffsets[][] = new int[ caseSets.length   ]
                                      [ Rank.VALUES.length ];
        for (int i = 0; i < caseSets.length; i++)
        {
            for (int j = 0; j < Rank.VALUES.length; j++)
            {
                CanonSuitSet caseSet = caseSets[ i ][ j ];
                if (caseSet == null)
                {
                    codedOffsets[ i ][ j ] = -1;
                }
                else
                {
                    codedOffsets[ i ][ j ] =
                            Codac.encodeTurn(caseSet, offset);
                    offset += caseSet.size();
                }
            }
        }
        return codedOffsets;
    }


    //--------------------------------------------------------------------
    public static int canonIndex(
            int flopIndex, CanonCard turn)
    {
        int codedOffset = CODED_OFFSET[ flopIndex             ]
                                      [ turn.rank().ordinal() ];
        return Codac.decodeTurnOffset(codedOffset) +
               Codac.decodeTurnSet(codedOffset).index( turn.suit() );
    }

//    public static CanonSuitSet caseSet(
//            int flopIndex, Card turnCard)
//    {
//        return CASE_SETS[ flopIndex                 ]
//                        [ turnCard.rank().ordinal() ];
//    }
//
//    public static int globalOffset(
//            int flopIndex, Card turnCard)
//    {
//        return GLOBAL_OFFSET[ flopIndex                 ]
//                            [ turnCard.rank().ordinal() ];
//    }
}
