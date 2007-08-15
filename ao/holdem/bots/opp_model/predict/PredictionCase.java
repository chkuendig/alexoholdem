package ao.holdem.bots.opp_model.predict;

import ao.holdem.bots.opp_model.mix.MixedAction;
import ao.holdem.bots.util.ApproximateOddFinder;
import ao.holdem.bots.util.OddFinder;
import ao.holdem.bots.util.Odds;
import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.cards.Community;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.Snapshot;

/**
 * ONLY WORKS FOR POST-FLOP!!
 *
 * Immedate pot odds.
 * Bet Ratio: bets/(bets+calls).
 * Pot Ratio: amount_in / pot_size.
 * Committed in this Round.
 * Bets-To-Call == 0.
 * Bets-To-Call == 1.
 * Bets-To-Call >= 2.
 * Stage == FLOP.
 * Stage == TURN.
 * Stage == RIVER.
 * Last-Bets-To-Call > 0.
 * Last-Action == BET/RAISE.
 *
 * Number of opponents.
 * Number of active opponents.
 * Number of unacted opponents.
 * Flush Possible. (not in yet)
 * Ace on Board.
 * King on Board.
 * (#AKQ on Board) / (# Board Cards).
 *
 * Position (in card reciept order).
 * Position within active players.
 */
public class PredictionCase
{
    //--------------------------------------------------------------------
    private double immedatePotOdds;
    private double betRatio;
    private double potRatio;
    private double committedThisRoundBool;
    private double zeroBetsToCallBool;
    private double oneBetToCallBool;
    private double manyBetsToCallBool;
    private double flopStageBool;
    private double turnStageBool;
    private double riverStageBool;
    private double lastBetsToCallBool;
    private double lastActRaiseBool;
    private double numOppsFraction;
    private double numActiveOppsFraction;
    private double numUnactedOppsFraction;
    private double flushPossibleBool;
    private double aceOnBoardBool;
    private double kingOnBoardBool;
    private double aceQueenKingPercent;
    private double winPercent;

    private double position;
    private double activePosition;

    private TakenAction action;


    //--------------------------------------------------------------------
    public PredictionCase(
            Snapshot prev, TakenAction prevAct,
            Snapshot curr, TakenAction currAct,
            Community   community,
            Hole        hole)
    {
        immedatePotOdds =
                ((double) curr.toCall().smallBlinds()) /
                (curr.toCall().smallBlinds() +
                    curr.pot().smallBlinds());

        double raises = 4 - curr.remainingRaises();
        betRatio = raises / (raises + curr.numCalls() + 0.001);
        potRatio =
                ((double) curr.stakes().smallBlinds()) /
                          curr.pot().smallBlinds();
        committedThisRoundBool =
                asDouble(curr.latestRoundCommitment().smallBlinds() > 0);

        zeroBetsToCallBool =
                asDouble(curr.toCall().bets( curr.isSmallBet() ) == 0);
        oneBetToCallBool =
                asDouble(curr.toCall().bets( curr.isSmallBet() ) == 1);
        manyBetsToCallBool =
                asDouble(curr.toCall().bets( curr.isSmallBet() ) >= 2);

        flopStageBool  = asDouble(curr.round() == BettingRound.FLOP);
        turnStageBool  = asDouble(curr.round() == BettingRound.TURN);
        riverStageBool = asDouble(curr.round() == BettingRound.RIVER);

        lastBetsToCallBool =
                (prev.toCall().smallBlinds() > 0)
                ? 1.0 : 0.0;
        lastActRaiseBool =
                (prevAct == TakenAction.RAISE)
                ? 1.0 : 0.0;

        numOppsFraction        = curr.opponents().size() / 9.0;
        numActiveOppsFraction  = curr.activeOpponents().size() / 9.0;
        numUnactedOppsFraction = curr.unactedThisRound() / 10.0;

        flushPossibleBool   = asDouble(community.flushPossible());
        aceOnBoardBool      = asDouble(community.contains(Card.Rank.ACE));
        kingOnBoardBool     = asDouble(community.contains(Card.Rank.KING));
        aceQueenKingPercent =
                (asDouble(community.contains(Card.Rank.ACE)) +
                 asDouble(community.contains(Card.Rank.KING)) +
                 asDouble(community.contains(Card.Rank.QUEEN))) /
                        (community.knownCount());

        position = (curr.players().indexOf( curr.nextToAct() ) + 1) / 10.0;
        activePosition = (curr.activePlayers().indexOf( curr.nextToAct() ) + 1) / 10.0;

        OddFinder oddFinder = new ApproximateOddFinder();
        Odds odds = oddFinder.compute(
                        hole, community, curr.activeOpponents().size());
        winPercent = odds.strengthVsRandom();

        action = currAct;
    }

    private double asDouble(boolean bool)
    {
        return bool ? 1.0 : 0.0;
    }

    public double[] asNeuralInput()
    {
        return new double[] {
                immedatePotOdds,
                betRatio,
                potRatio,
                committedThisRoundBool,
                zeroBetsToCallBool,
                oneBetToCallBool,
                manyBetsToCallBool,
                flopStageBool,
                turnStageBool,
                riverStageBool,
                lastBetsToCallBool,
                lastActRaiseBool,
                numOppsFraction,
                numActiveOppsFraction,
                numUnactedOppsFraction,
                flushPossibleBool,
                aceOnBoardBool,
                kingOnBoardBool,
                aceQueenKingPercent,
                position,
                activePosition,
//                winPercent,
//                1 // bias
        };
    }


    public TakenAction outputAction()
    {
        return action;
    }
    public double[] neuralOutput()
    {
        return new MixedAction(action).weights(); 
    }
}
