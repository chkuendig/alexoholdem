package ao.holdem.def.state.env;

import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.model.cards.community.Community;
import ao.holdem.def.state.domain.BettingRound;

/**
 *
 */
public class GodEnvironment extends Environment
{
    //--------------------------------------------------------------------
    private final Hole holesByAwayFromDealer[];


    //--------------------------------------------------------------------
    public GodEnvironment(
            Environment env,
            Hole        holesByAwayFromDealer[])
    {
        super(env);
        this.holesByAwayFromDealer = holesByAwayFromDealer;
    }
    public GodEnvironment(
            Community    community,
            Player       byPosition[],
            int          yourPosition,
            int          toCall,
            int          pot,
            int          commit,
            int          remainingRaises,
            BettingRound round,
            Hole         holesByAwayFromDealer[],
            int          fromPositionToDistanceFromDealer[])
    {
        super(holesByAwayFromDealer[
                fromPositionToDistanceFromDealer[yourPosition]],
              community, byPosition, yourPosition,
              toCall, pot, commit, remainingRaises, round,
              fromPositionToDistanceFromDealer);

        this.holesByAwayFromDealer = holesByAwayFromDealer;
    }


    //--------------------------------------------------------------------
    public Hole holeOf(int awayFromFirstToAct)
    {
        return holesByAwayFromDealer[
                awayFromDealer(awayFromFirstToAct) ];
    }

    public Hole holeOfAbs(int awayFromDealer)
    {
        return holesByAwayFromDealer[ awayFromDealer ];
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        int    players       = opponentCount() + 1;
        String playerViews[] = new String[players];
        for (int position = 0; position < players; position++)
        {
            Player player     = playerAt( position );
            int    fromDealer = awayFromDealer( position );

            playerViews[ fromDealer ] =
                    "hole: " + holeOf(position) + ", " + player;
        }

        StringBuilder flatPlayerViews = new StringBuilder();
        for (String playerView : playerViews)
        {
            flatPlayerViews.append('[')
                           .append(playerView)
                           .append(']');
        }

        return "betting round: " + bettingRound() + ", " +
               "community: " + community() + ", " +
               flatPlayerViews;
    }
}
