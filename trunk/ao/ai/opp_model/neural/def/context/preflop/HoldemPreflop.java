package ao.ai.opp_model.neural.def.context.preflop;

import ao.ai.opp_model.neural.def.context.GenericContext;
import ao.ai.opp_model.neural.def.context.HoldemPostact;
import ao.holdem.def.state.domain.BettingRound;

/**
 *
 */
public abstract class HoldemPreflop extends HoldemPostact
{
    //--------------------------------------------------------------------
    public HoldemPreflop(GenericContext ctx)
    {
        super(ctx);
        assert ctx.round() == BettingRound.PREFLOP;
    }
}
