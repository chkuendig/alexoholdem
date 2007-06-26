package ao.holdem.bots;

import static ao.holdem.def.model.card.Card.Rank.*;
import ao.holdem.def.bot.AbstractBot;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.state.action.Action;
import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.env.Environment;

/**
 * Strategy from
 *  http://www.pokertips.org/strategy/longhand.php
 */
public class PokerTipsBot extends AbstractBot
{
    //--------------------------------------------------------------------
    public Action act(Environment env)
    {
        Hole hole = env.hole();

        if (env.bettingRound() == BettingRound.PREFLOP)
        {
            // AA, KK, QQ, JJ, AK
            if (    hole.ranks(ACE,   ACE)   ||
                    hole.ranks(KING,  KING)  ||
                    hole.ranks(QUEEN, QUEEN) ||
                    hole.ranks(JACK,  JACK)  ||
                    hole.ranks(ACE,   KING))
            {
                return Action.RAISE_OR_CALL;
            }

            // TT, 99, AQ, KQ
            if (    hole.ranks(TEN,  TEN)   ||
                    hole.ranks(NINE, NINE)  ||
                    hole.ranks(ACE,  QUEEN) ||
                    hole.ranks(KING, QUEEN))
            {
                // raise, but don't reraise. Do not call 3 bets cold.
                if (env.bets() >= 3)
                {
                    return Action.CHECK_OR_FOLD;
                }
                else
                {
                    return Action.RAISE_OR_CALL;
                }
            }

            // 88, AJ, AT, KJ, QJ, JT, QT
            if (    hole.ranks(EIGHT, EIGHT) ||
                    hole.ranks(ACE,   JACK)  ||
                    hole.ranks(ACE,   TEN)   ||
                    hole.ranks(KING,  JACK)  ||
                    hole.ranks(QUEEN, JACK)  ||
                    hole.ranks(JACK,  TEN)   ||
                    hole.ranks(QUEEN, TEN))
            {
                if (env.bets() >= 2)
                {
                    return Action.CHECK_OR_FOLD;
                }
                else
                {
                    return Action.RAISE_OR_CALL;
                }
            }
        }
        return Action.CHECK_OR_FOLD;

//        if (env.realPosition() < 0.34) // early
//        {
//
//        }

//        // raise if both are hole cards are face cards,
//        //  or if the hole cards are a pocket pair.
//        if (hole.first().rank().compareTo(
//                TEN) > 0 &&
//                hole.second().rank().compareTo(
//                        TEN) > 0 ||
//                hole.first().rank().equals(
//                        hole.second().rank()))
//        {
//            return Action.RAISE_OR_CALL;
//        }
//        return Action.CHECK_OR_FOLD;
    }
}
