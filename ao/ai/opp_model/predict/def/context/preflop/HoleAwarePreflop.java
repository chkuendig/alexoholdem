package ao.ai.opp_model.predict.def.context.preflop;

import ao.ai.opp_model.predict.def.context.GenericContext;

/**
 *
 */
public class HoleAwarePreflop extends HoldemPreflop
{
    //--------------------------------------------------------------------
    public HoleAwarePreflop(GenericContext ctx)
    {
        super(ctx);
        addNeuralInput(ctx.winPercent());
    }
}
