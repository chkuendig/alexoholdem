package ao.bucket.index.iso_cards;

import ao.holdem.model.card.Rank;

/**
 * Date: Aug 23, 2008
 * Time: 2:55:42 PM
 */
public class WildCard implements RankedSuited<Rank, WildSuit>
{
    //--------------------------------------------------------------------
    public static WildCard newInstance(
            Rank     rank,
            WildSuit suit)
    {
        return new WildCard(rank, suit);
    }


    //--------------------------------------------------------------------
    private final Rank     RANK;
    private final WildSuit SUIT;


    //--------------------------------------------------------------------
    private WildCard(Rank     rank,
                     WildSuit suit)
    {
        RANK = rank;
        SUIT = suit;
    }


    //--------------------------------------------------------------------
    public WildMarkedCard mark(int leftRankMatches)
    {
        return WildMarkedCard.newInstance(
                RANK,
                WildMarkedSuit.newInstance(
                        SUIT, leftRankMatches));
    }


    //--------------------------------------------------------------------
    public Rank rank()
    {
        return RANK;
    }

    public WildSuit suit()
    {
        return SUIT;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return RANK + " of " + SUIT;
    }
}
