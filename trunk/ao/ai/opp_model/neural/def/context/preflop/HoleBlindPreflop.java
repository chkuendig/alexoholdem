package ao.ai.opp_model.neural.def.context.preflop;

import ao.ai.opp_model.neural.def.context.GenericContext;
import ao.holdem.def.state.domain.BettingRound;

/**
 *
 */
public class HoleBlindPreflop extends HoldemPreflop
{
    //--------------------------------------------------------------------
    public HoleBlindPreflop(GenericContext ctx)
    {
        super(ctx);
        assert ctx.round() == BettingRound.PREFLOP;
    }
}
