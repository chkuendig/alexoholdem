package ao.bucket.index.test;

import ao.bucket.index.Indexer;
import ao.bucket.index.incremental.IndexerImpl;
import ao.bucket.index.iso_flop.IsoFlop;
import ao.bucket.index.iso_turn.IsoTurn;
import ao.bucket.index.iso_turn.TurnCase;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;
import ao.holdem.model.card.sequence.LiteralCardSequence;
import ao.util.data.Arr;
import static ao.util.data.Arr.swap;
import ao.util.stats.FastIntCombiner;
import ao.util.stats.FastIntCombiner.CombinationVisitor2;
import ao.util.stats.FastIntCombiner.CombinationVisitor3;

import java.util.BitSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Date: Aug 21, 2008
 * Time: 7:05:20 PM
 */
public class GenericIndexerTest
{
    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        GenericIndexerTest indexerTest = new GenericIndexerTest();

        indexerTest.test( new IndexerImpl() );
    }


    //--------------------------------------------------------------------
    private Card cards[]   = Card.values();
    private int  indexes[] = Arr.sequence( Card.VALUES.length );


    //--------------------------------------------------------------------
    public void test(final Indexer indexer)
    {
        final Gapper flopGapper = new Gapper();
        final BitSet seenHoles  = new BitSet();

        final Set<TurnCase> turnCases = new LinkedHashSet<TurnCase>();
        new FastIntCombiner(indexes, indexes.length).combine(
                new CombinationVisitor2() {
            public void visit(int holeA, int holeB)
            {
                Hole hole = Hole.valueOf(
                        cards[holeA], cards[holeB]);

//            if (! hole.paired()) continue;
//            if (! hole.suited()) continue;
//            if (hole.suited() || hole.paired()) continue; // unsuited
                if (seenHoles.get( hole.suitIsomorphicIndex() )) return;
                seenHoles.set( hole.suitIsomorphicIndex() );

                swap(cards, holeB, 51  );
                swap(cards, holeA, 51-1);

                iterateFlops(hole, indexer, flopGapper, turnCases);

                swap(cards, holeA, 51-1);
                swap(cards, holeB, 51  );
            }
        });

        System.out.println("Flop gapper status:");
        flopGapper.displayStatus();

        System.out.println("Turn Cases:");
        for (TurnCase turnCase : turnCases)
        {
            System.out.println(turnCase);
        }
    }


    //--------------------------------------------------------------------
    public void iterateFlops(
            final Hole    hole,
            final Indexer indexer,
            final Gapper  flopGapper,
            final Set<TurnCase> turnCases
            //,FlopCase filter
            )
    {
        final BitSet seenFlops = new BitSet();

        new FastIntCombiner(indexes, indexes.length - 2).combine(
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
                int index = indexer.indexOf(cardSeq);
//                flopGapper.set(index);
//                if (index == 0) System.out.println(cardSeq);

                if (! seenFlops.get( index )) {
                    seenFlops.set( index );
                    iterateTurns(hole, flopCards, turnCases);
                }

                swap(cards, flopA, 51-4);
                swap(cards, flopB, 51-3);
                swap(cards, flopC, 51-2);
            }});

        System.out.println(hole);
    }

    public void iterateTurns(
            Hole    hole,
            Card    flop[],
            Set<TurnCase> turnCases)
    {
        IsoFlop isoFlop = hole.isoFlop( flop );
//        System.out.println(isoFlop.flopCase());

        Gapper turnGappers[] = new Gapper[4];
        //for (Card turnCard[] : new Combiner<Card>(cards, 47, 1))
        for (int turnCardIndex = 0;
                 turnCardIndex < 52 - 2 - 3;
                 turnCardIndex++)
        {
            Card turnCard = cards[ turnCardIndex ];
            IsoTurn isoTurn =
                    isoFlop.isoTurn(
                            hole.asArray(), flop, turnCard);

            Gapper turnGapper = turnGappers[ isoTurn.caseIndex() ];
            if (turnGapper == null)
            {
                turnGapper = new Gapper();
                turnGappers[ isoTurn.caseIndex() ] = turnGapper;
            }
            turnGapper.set( isoTurn.subIndex() );

//            TurnCase turnCase = isoTurn.turnCase();
//            if (turnCases.add( turnCase ))
//            {
//                System.out.println(turnCase);
//            }
        }

        for (Gapper turnGapper : turnGappers)
        {
            if (turnGapper == null) break;
            if (! turnGapper.continuous())
            {
                System.out.println("Turns after " + isoFlop);
                turnGapper.displayStatus();
            }
        }
    }
}
