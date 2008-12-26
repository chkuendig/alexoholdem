package ao.bucket.index;

import ao.bucket.index.flop.Flop;
import ao.bucket.index.river.River;
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
 */
public class CanonTraverser
{
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
            final short           canonHoles[],
            final Traverser<Flop> flopTraverser)
    {
        final BitSet holes = canonHoles == null
                             ? null : new BitSet();
        if (holes != null)
            for (int canonHole : canonHoles)
                holes.set( canonHole );

        final Card   cards[]   = Card.values();
        final BitSet seenFlops = new BitSet();

        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length).combine(
                new CombinationVisitor2() {
            public void visit(int holeA, int holeB)
            {
                Hole hole = Hole.valueOf(
                        cards[holeA], cards[holeB]);
                if ( holes != null &&
                    !holes.get( hole.canonIndex() )) return;

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
            int canonFlop, Traverser<Turn> turnTraverser)
    {

    }

    
    //--------------------------------------------------------------------
    public void traverseRiver(
            int canonTurn, Traverser<River> riverTraverser)
    {

    }
}
