package ao.holdem.bots;

import ao.holdem.def.bot.AbstractBot;
import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.card.eval7.Eval7Faster;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.model.cards.community.Community;
import ao.holdem.def.state.action.Action;
import ao.holdem.def.state.env.Environment;
import ao.util.stats.Combiner;

import java.util.Arrays;

/**
 * Designed for heads up play.
 */
public class MathBot extends AbstractBot
{
    //--------------------------------------------------------------------
    public Action act(Environment env)
    {
        if (env.opponentCount() != 1)
        {
            return Action.CHECK_OR_FOLD;
        }

        Hole      hole      = env.hole();
//        Community community = env.community();

        Card cards[] = Card.values().clone();

        swap(cards, hole.first().ordinal(),  cards.length - 1);
        swap(cards, hole.second().ordinal(), cards.length - 2);

        int overVals = 0;
        int communityCards = 0;

        Community community = env.community();
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

        System.out.println("overVals: " + overVals);
        return (overVals > 0)
                ? Action.RAISE_OR_CALL
                : Action.CHECK_OR_FOLD;
    }
}
