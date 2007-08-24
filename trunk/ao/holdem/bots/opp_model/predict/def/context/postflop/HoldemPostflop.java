package ao.holdem.bots.opp_model.predict.def.context.postflop;

import static ao.holdem.bots.opp_model.predict.def.NeuralUtils.asDouble;
import ao.holdem.bots.opp_model.predict.def.context.GenericContext;
import ao.holdem.bots.opp_model.predict.def.context.HoldemPostact;
import ao.holdem.def.state.domain.BettingRound;

/**
 *
 */
public abstract class HoldemPostflop extends HoldemPostact
{
    //--------------------------------------------------------------------
    public HoldemPostflop(GenericContext ctx)
    {
        super(ctx);
        assert ctx.round() != BettingRound.PREFLOP;

        double flopStageBool  =
                asDouble(ctx.round() == BettingRound.FLOP);
        double turnStageBool  =
                asDouble(ctx.round() == BettingRound.TURN);
        double riverStageBool =
                asDouble(ctx.round() == BettingRound.RIVER);

        addNeuralInput(flopStageBool,
                       turnStageBool,
                       riverStageBool);
    }
}
