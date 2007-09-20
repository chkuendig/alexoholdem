package ao.ai.opp_model.neural.def.context.postflop;

import static ao.ai.opp_model.neural.def.NeuralUtils.asDouble;
import ao.ai.opp_model.neural.def.context.GenericContext;
import ao.ai.opp_model.neural.def.context.HoldemPostact;
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
