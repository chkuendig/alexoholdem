package ao.bucket.abstraction.bucketize.smart;

import ao.bucket.abstraction.access.tree.LongByteList;
import ao.bucket.index.canon.hole.CanonHole;
import ao.bucket.index.canon.hole.HoleLookup;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Rank;
import ao.holdem.model.card.Suit;

/**
 * User: alex
 * Date: 1-Aug-2009
 * Time: 2:09:26 PM
 *
 * Display bucketing information
 */
public class BucketDisplay
{
    //--------------------------------------------------------------------
    private BucketDisplay() {}


    //--------------------------------------------------------------------
    public static void displayHoleBuckets(LongByteList holeBuckets)
    {
        for (Rank r : Rank.VALUES_REVERSE) {
            System.out.print("\t" + r);
        }
        for (Rank r : Rank.VALUES_REVERSE) {
            System.out.print("\n" + r + "\t");

            for (Rank rB : Rank.VALUES_REVERSE) {

                Card cardA = Card.valueOf(r, Suit.CLUBS);
                Card cardB = Card.valueOf(rB,
                        (r.ordinal() > rB.ordinal())
                        ? Suit.CLUBS : Suit.DIAMONDS);
                CanonHole hole = HoleLookup.lookup(cardA, cardB);

                System.out.print(
                        holeBuckets.get(hole.canonIndex()) + "\t");
            }
        }
    }
}
