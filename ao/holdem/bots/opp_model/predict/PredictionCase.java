package ao.holdem.bots.opp_model.predict;

import ao.holdem.def.model.cards.Community;
import ao.holdem.def.model.card.Card;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.Snapshot;

/**
 * ONLY WORKS FOR POST-FLOP!!
 *
 * Immedate pot odds.
 * Bet Ratio: bets/(bets+calls). (not in yet)
 * Pot Ratio: amount_in / pot_size.
 * Committed in this Round. (not in yet)
 * Bets-To-Call == 0 (not in yet)
 * Bets-To-Call == 1 (not in yet)
 * Bets-To-Call >= 2 (not in yet)
 * Stage == FLOP (not in yet)
 * Stage == TURN (not in yet)
 * Stage == RIVER (not in yet)
 * Last-Bets-To-Call > 0.
 * Last-Action == BET/RAISE.
 *
 * Number of opponents.
 * Number of active opponents.
 * Number of unacted opponents. (not in yet)
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
    private double potRatio;
    private double lastBetsToCallBool;
    private double lastActRaiseBool;
    private double aceOnBoardBool;
    private double kingOnBoardBool;
    private double aceQueenKingPercent;
    private double numOppsFraction;
    private double numActiveOppsFraction;
    private double position;
    private double activePosition;
//    private double

    private TakenAction action;


    //--------------------------------------------------------------------
    //Pot Ratio: amount_in / pot_size.
    public PredictionCase(
            Snapshot prev, TakenAction prevAct,
            Snapshot curr, TakenAction currAct,
            Community community)
    {
        immedatePotOdds =
                ((double) curr.toCall().smallBlinds()) /
                (curr.toCall().smallBlinds() +
                    curr.pot().smallBlinds());

        potRatio =
                (double) curr.stakes().smallBlinds() /
                         curr.pot().smallBlinds();

        lastBetsToCallBool =
                (prev.toCall().smallBlinds() > 0)
                ? 1.0 : 0.0;

        lastActRaiseBool =
                (prevAct == TakenAction.RAISE)
                ? 1.0 : 0.0;

        aceOnBoardBool =  asDouble(community.contains(Card.Rank.ACE));
        kingOnBoardBool = asDouble(community.contains(Card.Rank.KING));
        aceQueenKingPercent =
                (asDouble(community.contains(Card.Rank.ACE)) +
                 asDouble(community.contains(Card.Rank.KING)) +
                 asDouble(community.contains(Card.Rank.QUEEN))) /
                        (community.knownCount());

        numOppsFraction = curr.opponents().size() / 9.0;
        numActiveOppsFraction = curr.activeOpponents().size() / 9.0;

        position = (curr.players().indexOf( curr.nextToAct() ) + 1) / 10.0;
        activePosition = (curr.activePlayers().indexOf( curr.nextToAct() ) + 1) / 10.0;

        action = currAct;
    }

    private double asDouble(boolean bool)
    {
        return bool ? 1.0 : 0.0;
    }

    public double[] asNeatInput()
    {
        return new double[] {
                immedatePotOdds,
                potRatio,
                lastBetsToCallBool,
                lastActRaiseBool,
                aceOnBoardBool,
                kingOnBoardBool,
                aceQueenKingPercent,
                numOppsFraction,
                numActiveOppsFraction,
                position,
                activePosition,

                1 // bias
        };
    }


    public TakenAction outputAction()
    {
        return action;
    }
}
