package ao.ai.opp_model.neural.def.context.preflop;

import ao.ai.opp_model.neural.def.context.GenericContext;

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
