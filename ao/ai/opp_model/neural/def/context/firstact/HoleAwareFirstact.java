package ao.ai.opp_model.neural.def.context.firstact;

import ao.ai.opp_model.neural.def.context.GenericContext;
import ao.ai.opp_model.neural.def.context.HoldemPreact;

/**
 *
 */
public class HoleAwareFirstact extends HoldemPreact
{
    public HoleAwareFirstact(GenericContext ctx)
    {
        super(ctx);
        addNeuralInput(ctx.winPercent());
    }
}
