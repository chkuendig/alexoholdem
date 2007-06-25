package ao.holdem.tourney;

import ao.holdem.def.bot.BotProvider;
import ao.holdem.def.bot.LocalBot;
import ao.holdem.def.bot.Team;
import ao.holdem.game.Holdem;
import ao.holdem.game.Outcome;
import ao.holdem.game.impl.HoldemImpl;
import ao.util.rand.Rand;

import java.io.IOException;
import java.io.OutputStream;


/**
 * Runs a competition between Bots.
 * Groups their winnins/losings based on the Domain for which
 *  they were invoked.
 */
public class Tourney
{
    //--------------------------------------------------------------------
    private final BotProvider BOTS;
    private final Scoreboard  SCORES;


    //--------------------------------------------------------------------
    public Tourney(BotProvider bots)
    {
        BOTS   = bots;
        SCORES = Scoreboard.getInstance();
    }


    //--------------------------------------------------------------------
    public void runRandom()
    {
        int numPlayers = 2 + Rand.nextInt(9);
//        int numPlayers = 4;
        int numRounds  = 20 / numPlayers;

        run(numPlayers, numRounds);
    }


    //--------------------------------------------------------------------
    public void run(int numPlayers, int numRounds)
    {
        Holdem holdem = new HoldemImpl();
        holdem.configure(numPlayers, BOTS);

        for (int round = 0; round < numRounds; round++)
        {
            Outcome outcome = holdem.play();

            Winnings sum = new Winnings();
            for (Team team : outcome.players())
            {
                Winnings winnings =
                        new Winnings(team.stackDelta(),
                                     team.members().size());
                for (LocalBot member : team.members())
                {
                    sum.add( winnings );
                    SCORES.update(member, winnings);
                }
            }

            if (! sum.isCloseToZero())
            {
                // big blind wins without taking action.               
//                for (Outcome.Step step : outcome.log())
//                {
//                    OutcomeStepRender.display( step );
//                }
//                System.out.println(round);
            }
        }
    }


    //--------------------------------------------------------------------
    public void tabDelimitedReport(OutputStream to)
    {
        try
        {
            SCORES.writeTabDelimited( to );
        }
        catch (IOException e)
        {
            throw new Error( e );
        }
    }
}
