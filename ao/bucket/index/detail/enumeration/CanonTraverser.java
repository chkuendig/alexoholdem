package ao.bucket.index.detail.enumeration;

import ao.bucket.index.flop.Flop;
import ao.bucket.index.flop.FlopLookup;
import ao.bucket.index.river.River;
import ao.bucket.index.test.Gapper;
import ao.bucket.index.turn.Turn;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;
import static ao.util.data.Arr.swap;
import ao.util.misc.Traverser;
import ao.util.stats.FastIntCombiner;
import ao.util.stats.FastIntCombiner.CombinationVisitor2;
import ao.util.stats.FastIntCombiner.CombinationVisitor3;

import java.util.BitSet;

/**
 * Date: Oct 14, 2008
 * Time: 5:54:06 PM
 *
 * note: NOT threadsafe
 */
public class CanonTraverser
{
    //--------------------------------------------------------------------
    private Card   cards[]    = Card.values();
    private Gapper seenHoles  = new Gapper();
    private Gapper seenFlops  = new Gapper();
    private Gapper seenTurns  = new Gapper();
    private Gapper seenRivers = new Gapper();


    //--------------------------------------------------------------------
    public void traverseHoles(Traverser<Hole> holeTraverser)
    {
        BitSet seen = new BitSet();
        for (Card holeA : Card.VALUES)
        {
            for (Card holeB : Card.VALUES)
            {
                if (holeA == holeB) continue;
                Hole hole = Hole.valueOf(holeA, holeB);

                int canonIndex = hole.canonIndex();
                if (seen.get( canonIndex )) continue;
                seen.set( canonIndex );

                holeTraverser.traverse( hole );
            }
        }
    }


    //--------------------------------------------------------------------
    public void traverseFlops(Traverser<Flop> flopTraverser)
    {
        traverseFlops(null, flopTraverser);
    }
    public void traverseFlops(
            final long[]          canonHoles,
            final Traverser<Flop> flopTraverser)
    {
        final BitSet includeHoles = canonHoles == null
                             ? null : new BitSet();
        if (includeHoles != null)
            for (long canonHole : canonHoles)
                includeHoles.set( (int)canonHole );

        final Card   cards[]   = Card.values();
        final BitSet seenHoles = new BitSet();
        final BitSet seenFlops = new BitSet();

        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length).combine(
                new CombinationVisitor2() {
            public void visit(int holeA, int holeB)
            {
                Hole hole = Hole.valueOf(
                        cards[holeA], cards[holeB]);
                if ( includeHoles != null &&
                    !includeHoles.get( hole.canonIndex() ) ||
                     seenHoles.get(    hole.canonIndex() )) return;
                seenHoles.set( hole.canonIndex() );
                
                swap(cards, holeB, 51  );
                swap(cards, holeA, 51-1);

                iterateFlops(hole, cards, seenFlops, flopTraverser);

                swap(cards, holeA, 51-1);
                swap(cards, holeB, 51  );
            }
        });
    }

    private void iterateFlops(
            final Hole            hole,
            final Card            cards[],
            final BitSet          seenFlops,
            final Traverser<Flop> traverser)
    {
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length - 2)
                .combine(new CombinationVisitor3() {
            public void visit(int flopA, int flopB, int flopC)
            {
                Flop flop = hole.addFlop(
                        cards[flopA], cards[flopB], cards[flopC]);

                int flopIndex = flop.canonIndex();
                if (seenFlops.get( flopIndex )) return;
                seenFlops.set( flopIndex );

                traverser.traverse( flop );
            }});
    }


    //--------------------------------------------------------------------
    public void traverseTurns(
            final long[]          canonFlops,
            final Traverser<Turn> turnTraverser)
    {
        seenHoles.clear();
        seenTurns.clear();
        seenRivers.clear();

        final Gapper includeFlops = new Gapper();
        if (canonFlops != null) {
            for (long canonFlop : canonFlops)
                includeFlops.set( canonFlop );
        } else {
            for (long i = 0; i < FlopLookup.CANONICAL_COUNT; i++)
                includeFlops.set( i );
        }

        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length).combine(
                new CombinationVisitor2() {
            public void visit(int holeA, int holeB)
            {
                Hole hole = Hole.valueOf(
                        cards[holeA], cards[holeB]);

                if (seenHoles.get( hole.canonIndex() )) return;
                seenHoles.set( hole.canonIndex() );

                swap(cards, holeB, 51  );
                swap(cards, holeA, 51-1);

                iterateFlops(hole, includeFlops, turnTraverser);

                swap(cards, holeA, 51-1);
                swap(cards, holeB, 51  );
            }
        });
    }

    public void iterateFlops(
            final Hole            hole,
            final Gapper          includeFlops,
            final Traverser<Turn> turnTraverser)
    {
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length - 2).combine(
                new CombinationVisitor3() {
            public void visit(int flopA, int flopB, int flopC)
            {
                Flop flop = hole.addFlop(
                        cards[flopA], cards[flopB], cards[flopC]);
                int index = flop.canonIndex();

                if (seenFlops.get( index ) ||
                        !includeFlops.get(index)) return;
                seenFlops.set( index );

                swap(cards, flopC, 51-2);
                swap(cards, flopB, 51-3);
                swap(cards, flopA, 51-4);

                iterateTurns(flop, turnTraverser);

                swap(cards, flopA, 51-4);
                swap(cards, flopB, 51-3);
                swap(cards, flopC, 51-2);
            }});
    }

    public void iterateTurns(
            Flop            flop,
            Traverser<Turn> turnTraverser)
    {
        for (int turnCardIndex = 0;
                 turnCardIndex <= 51 - 5;
                 turnCardIndex++)
        {
            Card turnCard  = cards[ turnCardIndex ];
            Turn turn      = flop.addTurn(turnCard);
            int  turnIndex = turn.canonIndex();

            if (seenTurns.get( turnIndex )) continue;
            seenTurns.set( turnIndex );

            turnTraverser.traverse( turn );
        }
    }


    //--------------------------------------------------------------------
    public void traverseRiver(
            int canonTurn, Traverser<River> riverTraverser)
    {

    }
}
