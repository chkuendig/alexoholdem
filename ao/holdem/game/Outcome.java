package ao.holdem.game;

import ao.holdem.def.bot.Bot;
import ao.holdem.def.state.env.GodEnvironment;
import ao.holdem.def.state.env.TakenAction;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class Outcome
{
    //--------------------------------------------------------------------
    private final List<Step> STATES;


    //--------------------------------------------------------------------
    public Outcome()
    {
        STATES = new ArrayList<Step>();
    }


    //--------------------------------------------------------------------
    public void add(Bot bot, GodEnvironment env, TakenAction action)
    {
        STATES.add( new Step(bot, env, action) );
    }

    public void add(GodEnvironment env)
    {
        add(null, env, null);
    }


    //--------------------------------------------------------------------
    public List<Step> log()
    {
        return STATES;
    }


    //--------------------------------------------------------------------
    /**
     * Actions taken by each bot at each round of betting.
     * So actions()[0] contains actions for the pre-lop etc.
     * If a Bot folds, then consequent actions are
     *  TakenAction.DID_NOT_ACT.
     * @return ...
     */
//    public List<Map<Bot, TakenAction>> actions()
//    {
//        return null;
//    }


    //--------------------------------------------------------------------
//    public Community community()
//    {
//        return null;
//    }


    //--------------------------------------------------------------------
//    public Map<Bot, Hole> holes()
//    {
//        return null;
//    }


    //--------------------------------------------------------------------
//    public List<Bot> winner()
//    {
//        return null;
//    }


    //--------------------------------------------------------------------
    public static class Step
    {
        private final Bot            BOT;
        private final GodEnvironment ENV;
        private final TakenAction    ACTION;

        public Step(Bot bot, GodEnvironment env, TakenAction action)
        {
            BOT    = bot;
            ENV    = env;
            ACTION = action;

//            if ENV.activeOpponents()
        }

        public Bot actor()
        {
            return BOT;
        }

        public GodEnvironment environment()
        {
            return ENV;
        }

        public TakenAction action()
        {
            return ACTION;
        }

        public boolean isParcial()
        {
            return BOT == null || ACTION == null || ENV == null;
        }
    }
}
