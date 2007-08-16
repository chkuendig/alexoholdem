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
        assert curr.comingRound() != BettingRound.PREFLOP;

        double flopStageBool  =
                asDouble(curr.comingRound() == BettingRound.FLOP);
        double turnStageBool  =
                asDouble(curr.comingRound() == BettingRound.TURN);
        double riverStageBool =
                asDouble(curr.comingRound() == BettingRound.RIVER);

        addNeuralInput(flopStageBool,
                       turnStageBool,
                       riverStageBool);
    }
}
