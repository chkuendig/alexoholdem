package ao.bucket.index.iso_cards.wild.card;

import ao.bucket.index.iso_cards.Ordering;
import ao.bucket.index.iso_cards.wild.suit.WildMarkedSuit;
import ao.bucket.index.iso_cards.wild.suit.WildSuit;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Rank;

/**
 * Date: Aug 23, 2008
 * Time: 2:55:42 PM
 */
public class WildCard extends RankedSuitedImpl<WildSuit>
{
    //--------------------------------------------------------------------
    public static WildCard newInstance(
            Ordering order, Card card)
    {
        return WildCard.newInstance(
                card.rank(),
                order.asWild( card.suit() ));
    }

    public static WildCard newInstance(
            Rank     rank,
            WildSuit suit)
    {
        return new WildCard(rank, suit);
    }


    //--------------------------------------------------------------------
    private WildCard(Rank     rank,
                     WildSuit suit)
    {
        super(rank, suit);
    }


    //--------------------------------------------------------------------
    public RankedSuited<Rank, WildMarkedSuit>
            mark(int leftRankMatches)
    {
        return new RankedSuitedImpl<WildMarkedSuit>(
                rank(),
                WildMarkedSuit.newInstance(
                        suit(), leftRankMatches));
    }
}
