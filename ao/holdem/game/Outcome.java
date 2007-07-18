package ao.holdem.game;

import ao.holdem.def.bot.LocalBot;
import ao.holdem.def.bot.Team;
import ao.holdem.def.model.cards.Hand;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.model.cards.Community;
import ao.holdem.def.state.env.Environment;
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
    public void add(LocalBot       bot,
                    GodEnvironment env,
                    TakenAction    action)
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

    public int pot()
    {
        return STEPS.get( STEPS.size() - 1 ).environment().pot();
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

            Hand hand = new Hand(hole, community);
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
//    public
    

    //--------------------------------------------------------------------
    public List<Team> players()
    {
        List<Team> players = new ArrayList<Team>();
        players.addAll( winners() );
        players.addAll( losers()  );
        return players;
    }

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
    public List<Team> winners()
    {
        return byAwayFromDealer( true );
    }

    public List<Team> losers()
    {
        return byAwayFromDealer( false );
    }

    @SuppressWarnings("unchecked")
    private List<Team> byAwayFromDealer(boolean winners)
    {
        int numPlayers = STEPS.get(0).environment().playerCount();
        List<Team> byAwayFromDealer = new ArrayList<Team>(numPlayers);
        for (int i = 0; i < numPlayers; i++)
        {
            byAwayFromDealer.add( new Team() );
        }

        int winnerCount = winnerCount();
        for (Step step : STEPS)
        {
            Environment env    = step.environment();
            int awayFromDealer = env.awayFromDealer();
            if (WINNERS[ awayFromDealer ] == winners)
            {
                Team team = byAwayFromDealer.get( awayFromDealer );

                if (! step.isParcial())
                {
                    team.add( step.actor() );
                }
                team.setStackDelta(
                        stackDeltaFor(env, winnerCount, winners) );
            }
        }

        for (Team team :
                new ArrayList<Team>(byAwayFromDealer))
        {
            if (team.members().isEmpty())
            {
                byAwayFromDealer.remove( team );
            }
        }
        return byAwayFromDealer;
    }

    private double stackDeltaFor(Environment env,
                                 int         numWinners,
                                 boolean     winners)
    {
        return (winners ? ((double)env.pot())/numWinners : 0)
                - env.commit();
    }
    private int winnerCount()
    {
        int winnerCount = 0;
        for (boolean isWinner : WINNERS)
        {
            if (isWinner) winnerCount++;
        }
        return winnerCount;
    }


    //--------------------------------------------------------------------
    public static class Step
    {
        private final LocalBot       BOT;
        private final GodEnvironment ENV;
        private final TakenAction    ACTION;
        private final boolean        IS_WINNER;

        public Step(LocalBot       bot,
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

        public LocalBot actor()
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
