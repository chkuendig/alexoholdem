package ao.holdem.bots.opp_model.predict.def.context.postflop;

import ao.holdem.history.Snapshot;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.bots.opp_model.predict.def.context.HoldemPostact;
import static ao.holdem.bots.opp_model.predict.def.NeuralUtils.asDouble;

/**
 *
 */
public abstract class HoldemPostflop extends HoldemPostact
{
    //--------------------------------------------------------------------
    public HoldemPostflop(
            Snapshot    prev,
            TakenAction prevAct,
            Snapshot    curr)
    {
        super(prev, prevAct, curr);
        assert curr.round() != BettingRound.PREFLOP;

        double flopStageBool  =
                asDouble(curr.round() == BettingRound.FLOP);
        double turnStageBool  =
                asDouble(curr.round() == BettingRound.TURN);
        double riverStageBool =
                asDouble(curr.round() == BettingRound.RIVER);

        addNeuralInput(flopStageBool,
                       turnStageBool,
                       riverStageBool);
    }
}
