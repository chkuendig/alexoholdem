package ao.bucket.index.enumeration;

import ao.bucket.index.flop.Flop;
import ao.bucket.index.hole.CanonHole;
import ao.bucket.index.hole.HoleLookup;
import ao.bucket.index.river.River;
import ao.bucket.index.turn.Turn;
import ao.holdem.model.card.Card;
import static ao.util.data.Arr.swap;
import ao.util.math.stats.FastIntCombiner;
import ao.util.math.stats.FastIntCombiner.CombinationVisitor2;
import ao.util.math.stats.FastIntCombiner.CombinationVisitor3;
import ao.util.misc.Filter;
import ao.util.misc.Traverser;

/**
 * Date: Jan 21, 2009
 * Time: 12:37:11 PM
 *
 * NOTE: not nestable and not threadsafe
 */
public class CardEnum
{
    //--------------------------------------------------------------------
    private CardEnum() {}


    //--------------------------------------------------------------------
    private static final Card[] CARDS = Card.values();


    //--------------------------------------------------------------------
    public static void traverseHoles(
            final Filter<CanonHole>    holeFilter,
            final Traverser<CanonHole> holeTraverser)
    {
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length)
                .combine(new CombinationVisitor2() {
            public void visit(int holeA, int holeB)
            {
                CanonHole canonHole = HoleLookup.lookup(
                        CARDS[holeA], CARDS[holeB]);
                if (holeFilter.accept(canonHole))
                {
                    swap(CARDS, holeB, 51  );
                    swap(CARDS, holeA, 51-1);

                    holeTraverser.traverse( canonHole );

                    swap(CARDS, holeA, 51-1);
                    swap(CARDS, holeB, 51  );
                }
            }
        });
    }


    //--------------------------------------------------------------------
    public static void traverseFlops(
            final Filter<CanonHole> holeFilter,
            final Filter<Flop>      flopFilter,
            final Traverser<Flop>   flopTraverser)
    {
        traverseHoles(holeFilter, new Traverser<CanonHole>() {
            public void traverse(final CanonHole canonHole) {
                new FastIntCombiner(
                        Card.INDEXES, Card.INDEXES.length - 2).combine(
                        new CombinationVisitor3() {
                public void visit(int flopA, int flopB, int flopC) {
                    Flop flop = canonHole.addFlop(
                        CARDS[flopA], CARDS[flopB], CARDS[flopC]);

                    if (flopFilter.accept(flop))
                    {
                        swap(CARDS, flopC, 51-2);
                        swap(CARDS, flopB, 51-3);
                        swap(CARDS, flopA, 51-4);

                        flopTraverser.traverse(flop);

                        swap(CARDS, flopA, 51-4);
                        swap(CARDS, flopB, 51-3);
                        swap(CARDS, flopC, 51-2);
                    }
                }
            });
        }});
    }


    //--------------------------------------------------------------------
    public static void traverseUniqueTurns(
            Traverser<Turn> turnTraverser)
    {
        traverseTurns(new UniqueFilter<CanonHole>(),
                      new UniqueFilter<Flop>(),
                      new UniqueFilter<Turn>(),
                      turnTraverser);
    }
    public static void traverseTurns(
            final Filter<CanonHole> holeFilter,
            final Filter<Flop>      flopFilter,
            final Filter<Turn>      turnFilter,
            final Traverser<Turn>   turnTraverser)
    {
        traverseFlops(holeFilter, flopFilter, new Traverser<Flop>() {
            public void traverse(Flop flop) {
                for (int turnCardIndex = 0;
                         turnCardIndex <= 51 - 5;
                         turnCardIndex++)
                {
                    Card turnCard = CARDS[ turnCardIndex ];
                    Turn turn     = flop.addTurn(turnCard);

                    if (turnFilter.accept(turn))
                    {
                        swap(CARDS, turnCardIndex, 51 - 5);

                        turnTraverser.traverse(turn);

                        swap(CARDS, turnCardIndex, 51 - 5);
                    }
                }
            }
        });
    }


    //--------------------------------------------------------------------
    public static void traverseUniqueRivers(
            Traverser<River> riverTraverser)
    {
        traverseRivers(new UniqueFilter<CanonHole>(),
                       new UniqueFilter<Flop>(),
                       new UniqueFilter<Turn>(),
                       new UniqueFilter<River>(),
                       riverTraverser);
    }
    public static void traverseRivers(
            final Filter<CanonHole> holeFilter,
            final Filter<Flop>      flopFilter,
            final Filter<Turn>      turnFilter,
            final Filter<River>     riverFilter,
            final Traverser<River>  riverTraverser)
    {
        traverseTurns(holeFilter, flopFilter, turnFilter,
                      new Traverser<Turn>() {
            public void traverse(Turn turn) {
                for (int riverCardIndex = 0;
                         riverCardIndex <= 51 - 6;
                         riverCardIndex++)
                {
                    Card  riverCard  = CARDS[ riverCardIndex ];
                    River river      = turn.addRiver( riverCard );

                    if (riverFilter.accept(river))
                    {
                        riverTraverser.traverse(river);
                    }
                }
            }
        });
    }
}
