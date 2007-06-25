package ao.holdem.bots;

import ao.holdem.def.bot.AbstractBot;
import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.state.action.Action;
import ao.holdem.def.state.env.Environment;

/**
 *
 */
public class SimpleBot extends AbstractBot
{
    //--------------------------------------------------------------------
    public Action act(Environment env)
    {
//        System.out.println(toString() + " acting with " + env.hole());
//        return Action.CHECK_OR_CALL;

        Hole hole = env.hole();

        // raise if both are hole cards are face cards,
        //  or if the hole cards are a pocket pair.
        if (hole.first().rank().compareTo(
                Card.Rank.TEN) > 0 &&
                hole.second().rank().compareTo(
                        Card.Rank.TEN) > 0 ||
                hole.first().rank().equals(
                        hole.second().rank()))
        {
            return Action.RAISE_OR_CALL;
        }
        return Action.CHECK_OR_FOLD;
    }


    //--------------------------------------------------------------------
//    @Override
//    public String toString()
//    {
//        return "SimpleBot";
//    }
}
