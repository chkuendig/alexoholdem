package ao.holdem.bots.opp_model.predict.def.context.preflop;

import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.Snapshot;

/**
 *
 */
public class HoleBlindPreflop extends HoldemPreflop
{
    //--------------------------------------------------------------------
    public HoleBlindPreflop(
            Snapshot    prev,
            TakenAction prevAct,
            Snapshot    curr)
    {
        super(prev, prevAct, curr);
        assert curr.round() == BettingRound.PREFLOP;
    }
}
