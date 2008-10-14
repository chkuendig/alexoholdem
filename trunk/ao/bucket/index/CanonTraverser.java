package ao.bucket.index;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;
import ao.holdem.model.card.sequence.LiteralCardSequence;

import java.util.BitSet;

/**
 * Date: Oct 14, 2008
 * Time: 5:54:06 PM
 */
public class CanonTraverser
{
    //--------------------------------------------------------------------
    public void traverse(Traverser holeTraverser)
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

                holeTraverser.traverse(
                        new LiteralCardSequence(
                                hole, Community.PREFLOP));
            }
        }
    }


    //--------------------------------------------------------------------
    public void traverseFlops(int canonHole, Traverser flopTraverser)
    {

    }

    public void traverseTurns(int canonFlop, Traverser turnTraverser)
    {

    }

    public void traverseRiver(int canonTurn, Traverser riverTraverser)
    {

    }


    //--------------------------------------------------------------------
    public static interface Traverser
    {
        public void traverse(CardSequence cards);
    }
}
