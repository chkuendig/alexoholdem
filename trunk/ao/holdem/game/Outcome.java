package ao.holdem.game;

import ao.holdem.def.bot.Bot;
import ao.holdem.def.model.cards.Hand;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.model.cards.community.Community;
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
    private final List<Step> STEPS;
    private final boolean    WINNERS[];

    //--------------------------------------------------------------------
    public Outcome(int numPlayers)
    {
        STEPS   = new ArrayList<Step>();
        WINNERS = new boolean[ numPlayers ];
    }


    //--------------------------------------------------------------------
    public void add(Bot bot, GodEnvironment env, TakenAction action)
    {
        Step step = new Step(bot, env, action);
        STEPS.add( step );

        if (step.isWinner())
        {
            WINNERS[ env.awayFromDealer() ] = true;
        }
    }

    public void add(GodEnvironment env)
    {
        add(null, env, null);
    }


    //--------------------------------------------------------------------
    public List<Step> log()
    {
        return STEPS;
    }


    //--------------------------------------------------------------------
    public void showdown(List<Integer> byAwayFromDealer)
    {
        GodEnvironment lastEnv =
                STEPS.get(STEPS.size() - 1).environment();
        Community community = lastEnv.community();

        short         maxVal  = Short.MIN_VALUE;
        List<Integer> winners = new ArrayList<Integer>();
        for (int awayFromDealer : byAwayFromDealer)
        {
            Hole hole = lastEnv.holeOfAbs( awayFromDealer );

            Hand hand = new Hand(hole, community.river());
            if (hand.value() > maxVal)
            {
                maxVal = hand.value();
                winners.clear();
            }
            if (hand.value() == maxVal)
            {
                winners.add( awayFromDealer );
            }
        }

        for (int winner : winners)
        {
            WINNERS[ winner ] = true;
        }
    }


    //--------------------------------------------------------------------
    /**
     * @return A list of winning teams.  More than one team wins
     *          if there is a split pot.
     *          Each team consists of the bots that make it up, they are
     *          listed in action order.
     *         Note that if everybody folds to the big blind, then
     *          there is no winning bot (since no bot is designated
     *          to act for big blind), however all acting bots
     *          in this care are losers.
     */
    public List<List<Bot>> winners()
    {
        return byAwayFromDealer( true );
    }

    public List<List<Bot>> losers()
    {
        return byAwayFromDealer( false );
    }

    @SuppressWarnings("unchecked")
    private List<List<Bot>> byAwayFromDealer(boolean winners)
    {
        int numPlayers = STEPS.get(0).environment().playerCount();
        List<List<Bot>> byAwayFromDealer =
                new ArrayList<List<Bot>>(numPlayers);
        for (int i = 0; i < numPlayers; i++)
        {
            byAwayFromDealer.add( new ArrayList<Bot>() );
        }

        for (Step step : STEPS)
        {
            if (step.isParcial()) continue;

            int awayFromDealer = step.environment().awayFromDealer();
            if (WINNERS[ awayFromDealer ] == winners)
            {
                byAwayFromDealer.get( awayFromDealer )
                        .add( step.actor() );
            }
        }

        for (List<Bot> bots : new ArrayList<List<Bot>>(byAwayFromDealer))
        {
            if (bots.isEmpty())
            {
                byAwayFromDealer.remove( bots );
            }
        }
        return byAwayFromDealer;
    }


    //--------------------------------------------------------------------
    public static class Step
    {
        private final Bot            BOT;
        private final GodEnvironment ENV;
        private final TakenAction    ACTION;
        private final boolean        IS_WINNER;

        public Step(Bot            bot,
                    GodEnvironment env,
                    TakenAction    action)
        {
            BOT       = bot;
            ENV       = env;
            ACTION    = action;
            IS_WINNER = ENV.activeOpponents() == 0;
        }

        public boolean isWinner()
        {
            return IS_WINNER;
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
