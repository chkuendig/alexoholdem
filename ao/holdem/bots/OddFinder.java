package ao.holdem.bots;

import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.model.cards.community.Community;
import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.card.eval7.Eval7Faster;
import ao.util.stats.Combiner;

import java.util.Arrays;

/**
 * Calculates odds of winning/losing.
 */
public class OddFinder
{
    //--------------------------------------------------------------------
    private final int  knownCommunity;
    private final Card cards[] = Card.values().clone();
    private final int  opponents;


    //--------------------------------------------------------------------
    public OddFinder(Hole      hole,
                     Community community,
                     int       opponents)
    {
        moveHolesToEnd(cards, hole);
        knownCommunity = moveCommunityToEnd(cards, community);

        this.opponents = opponents;
    }


    //--------------------------------------------------------------------
    public long combos()
    {
        int oppCards         = 2 * opponents;
        int unknownCommunity = (5 - knownCommunity);
        int choose           = oppCards + unknownCommunity;

        return Combiner.choose(unknownCount(), choose);
    }

    private int unknownCount()
    {
        return 52 - knownCount();
    }

    private int knownCount()
    {
        return 2 + knownCommunity;
    }


    //--------------------------------------------------------------------
    // expected percent of not losing. (ie. winnin or tying)
    public double compute()
    {
        int unknown = unknownCount();

        // 0 known community
        // 1 opponent
        for (int c0 = 0; c0 < unknown; c0++)
        {
            
        }



        int knowCards    = communityCards + 2;
        int unknownCards = (2 + 2 + 5) - knowCards;
        Combiner<Card> combiner =
                new Combiner<Card>(
                        Arrays.copyOf(cards, 52 - knowCards),
                        unknownCards);

        Card combo[] = new Card[7];
        for (int i = 0; i < communityCards; i++)
        {
            combo[i] = cards[(52 - 2) - i];
        }

        while (combiner.hasMoreElements())
        {
            Card possible[] = combiner.nextElement();

            for (int i = 0; i < possible.length; i++)
            {
                combo[6 - i] = possible[i];
            }

            short oppVal = Eval7Faster.valueOf(combo);
            short myVal  = Eval7Faster.valueOf(
                    combo[0], combo[1], combo[2], combo[3],
                    combo[4], hole.first(), hole.second());

            if (myVal != oppVal)
            {
                overVals += (myVal > oppVal) ? 1 : -1;
            }
        }
        return 0;
    }


    //--------------------------------------------------------------------
    private void moveHolesToEnd(Card cards[], Hole hole)
    {
        swap(cards, hole.first().ordinal(),  cards.length - 1);
        swap(cards, hole.second().ordinal(), cards.length - 2);
    }

    private int moveCommunityToEnd(Card cards[], Community community)
    {
        int communityCards = 0;
        if (community.flop() != null)
        {
            communityCards = 3;
            swap(cards, community.flop().first().ordinal(),  cards.length - 3);
            swap(cards, community.flop().second().ordinal(), cards.length - 4);
            swap(cards, community.flop().third().ordinal(),  cards.length - 5);
        }
        if (community.turn() != null)
        {
            communityCards = 4;
            swap(cards, community.turn().fourth().ordinal(), cards.length - 6);
        }
        if (community.river() != null)
        {
            communityCards = 5;
            swap(cards, community.turn().fourth().ordinal(), cards.length - 7);
        }
        return communityCards;
    }


    //--------------------------------------------------------------------
    private static void swap(Card cards[], int i, int j)
    {
        Card temp = cards[i];
        cards[i] = cards[j];
        cards[j] = temp;
    }
}
