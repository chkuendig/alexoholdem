package ao.holdem.bots.opp_model.predict.def.context.postflop;

import ao.holdem.bots.opp_model.predict.def.context.GenericContext;

/**
 *
 */
public class HoleAwarePostflop extends HoldemPostflop
{
    //--------------------------------------------------------------------
    public HoleAwarePostflop(GenericContext ctx)
    {
        super(ctx);
        addNeuralInput(ctx.winPercent());
    }
}