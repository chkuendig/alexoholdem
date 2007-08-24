package ao.holdem.bots.opp_model.predict.def.context.preflop;

import ao.holdem.bots.opp_model.predict.def.context.GenericContext;
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
