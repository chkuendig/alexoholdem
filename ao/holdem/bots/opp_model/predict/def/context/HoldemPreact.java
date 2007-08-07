package ao.holdem.bots.opp_model.predict.def.context;

import static ao.holdem.bots.opp_model.predict.def.NeuralUtils.asDouble;
import ao.holdem.history.Snapshot;

/**
 *
 */
public class HoldemPreact extends AbstractContext
{
    //--------------------------------------------------------------------
    public HoldemPreact(Snapshot curr)
    {
        double immedatePotOdds =
                ((double) curr.toCall().smallBlinds()) /
                (curr.toCall().smallBlinds() +
                    curr.pot().smallBlinds());

        double raises = 4 - curr.remainingRaises();
        double betRatio = raises / (raises + curr.numCalls() + 0.001);
        double potRatio =
                ((double) curr.stakes().smallBlinds()) /
                          curr.pot().smallBlinds();
        double committedThisRoundBool =
                asDouble(curr.latestRoundCommitment().smallBlinds() > 0);

        double zeroBetsToCallBool =
                asDouble(curr.toCall().bets( curr.isSmallBet() ) == 0);
        double oneBetToCallBool =
                asDouble(curr.toCall().bets( curr.isSmallBet() ) == 1);
        double manyBetsToCallBool =
                asDouble(curr.toCall().bets( curr.isSmallBet() ) >= 2);

        double numOppsFraction = curr.opponents().size() / 9.0;
        double numActiveOppsFraction = curr.activeOpponents().size() / 9.0;
        double numUnactedOppsFraction = curr.unactedThisRound() / 10.0;

        double position =
                (curr.players().indexOf(
                        curr.nextToActLookahead() ) + 1) / 10.0;
        double activePosition =
                (curr.activePlayers().indexOf(
                        curr.nextToActLookahead() ) + 1) / 10.0;

        addNeuralInput(
                immedatePotOdds,
                betRatio,
                potRatio,
                committedThisRoundBool,
                zeroBetsToCallBool,
                oneBetToCallBool,
                manyBetsToCallBool,
                numOppsFraction,
                numActiveOppsFraction,
                numUnactedOppsFraction,
                position,
                activePosition);
    }
}
