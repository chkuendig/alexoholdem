package ao.bucket.index.turn;

import ao.bucket.index.card.CanonCard;
import ao.bucket.index.card.CanonSuit;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.flop.FlopLookup;
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
    public static final int CANON_TURN_COUNT = 55190538;


    //--------------------------------------------------------------------
    private static final String       RAW_CASE_FILE =
                                        "lookup/canon/turn.cases.cache";
    private static final int          CODED_OFFSET[][];

    static
    {
        TurnCase caseSets[][] =
                retrieveOrCalculateCaseSets();
        CODED_OFFSET = encodeOffsets(caseSets);
    }

    public static void main(String args[])
    {
        // initialize
    }


    //--------------------------------------------------------------------
    private static TurnCase[][] retrieveOrCalculateCaseSets()
    {
        System.out.println("TurnLookup.retrieveOrCalculateCaseSets");
        TurnCase[][] caseSets = retrieveCaseSets();
        if (caseSets == null)
        {
            caseSets = calculateCases();
            storeCaseSets(caseSets);
        }
        return caseSets;
    }

    private static TurnCase[][] retrieveCaseSets()
    {
        byte asBytes[] = PersistentBytes.retrieve(RAW_CASE_FILE);
        if (asBytes == null) return null;

        int          flatIndex    = 0;
        TurnCase caseSets[][] =
                new TurnCase
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
                            TurnCase.VALUES[ ordinal ];
                }
            }
        }
        return caseSets;
    }

    private static void storeCaseSets(TurnCase caseSets[][])
    {
        int  flatIndex = 0;
        byte asBytes[] = new byte[ caseSets.length * Rank.VALUES.length ];
        for (TurnCase[] caseSet : caseSets)
        {
            for (TurnCase aCaseSet : caseSet)
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
    private static TurnCase[][] calculateCases()
    {
        final TurnCase caseSets[][] =
                new TurnCase[ FlopLookup.CANON_FLOP_COUNT ]
                            [ /*  Rank.VALUES.length   */ ];

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
            final Hole     hole,
            final Card     cards[],
            final TurnCase caseSets[][])
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

//                Card flopCards[] =
//                        {cards[flopA], cards[flopB], cards[flopC]};

                swap(cards, flopC, 51-2);
                swap(cards, flopB, 51-3);
                swap(cards, flopA, 51-4);

                caseSets[ flopIndex ] =
                        iterateTurns(flop, cards);

                swap(cards, flopA, 51-4);
                swap(cards, flopB, 51-3);
                swap(cards, flopC, 51-2);
            }});
    }

    private static TurnCase[] iterateTurns(
            Flop flop,
            Card cards[])
    {
        TurnCase turnCases[] =
                new TurnCase[ Rank.VALUES.length ];

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

                Turn turn = flop.addTurn(turnCard);
                buffer.add( turn.turnSuit() );

//                System.out.println(
//                        cards[49] + "," + cards[48 ]+ "," + cards[47] +
//                        "|" + turnCard + "\t" +
//                        turn.turnCase());
            }

            if (! buffer.isEmpty())
            {
                turnCases[ rank.ordinal() ] =
                    TurnCase.valueOf(buffer);
            }
        }
        return turnCases;
    }


    //--------------------------------------------------------------------
    private static int[][] encodeOffsets(TurnCase caseSets[][])
    {
        System.out.println("TurnLookup.encodeOffsets");
        int offset           = 0;
        int codedOffsets[][] = new int[ caseSets.length   ]
                                      [ Rank.VALUES.length ];
        for (int i = 0; i < caseSets.length; i++)
        {
            for (int j = 0; j < Rank.VALUES.length; j++)
            {
                TurnCase caseSet = caseSets[ i ][ j ];
                if (caseSet == null)
                {
                    codedOffsets[ i ][ j ] = -1;
                }
                else
                {
                    codedOffsets[ i ][ j ] =
                            TurnUtil.encodeTurn(caseSet, offset);
                    offset += caseSet.size();
                }
            }
        }
        System.out.println("TurnLookup.encodeOffsets end " + offset);
        return codedOffsets;
    }


    //--------------------------------------------------------------------
    public static int canonIndex(
            int flopIndex, CanonCard turn)
    {
        int codedOffset = CODED_OFFSET[ flopIndex             ]
                                      [ turn.rank().ordinal() ];
        return TurnUtil.decodeTurnOffset(codedOffset) +
               TurnUtil.decodeTurnSet(codedOffset).index( turn.suit() );
    }

//    public static TurnCase caseSet(
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
