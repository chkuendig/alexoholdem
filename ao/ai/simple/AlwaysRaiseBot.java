package ao.ai.simple;

import ao.holdem.def.bot.AbstractBot;
import ao.holdem.model.act.Action;

/**
 *
 */
public class AlwaysRaiseBot extends AbstractBot
{
    //--------------------------------------------------------------------
    public Action act(Environment env)
    {
//        System.out.println(
//                "aways raising shane styles" +
//                " with " + env.hole() + " on " + env.community());
        return Action.RAISE_OR_CALL;
    }


    //--------------------------------------------------------------------
//    public String toString()
//    {
//        return "AlwaysRaiseBot";
//    }
}