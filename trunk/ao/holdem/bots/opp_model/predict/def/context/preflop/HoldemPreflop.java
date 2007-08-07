package ao.holdem.bots.opp_model.predict.def.context.preflop;

import ao.holdem.bots.opp_model.predict.def.context.HoldemPostact;
import ao.holdem.history.Snapshot;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.def.state.domain.BettingRound;

/**
 *
 */
public abstract class HoldemPreflop extends HoldemPostact
{
    //--------------------------------------------------------------------
    public HoldemPreflop(
            Snapshot prev,
            TakenAction prevAct,
            Snapshot    curr)
    {
        super(prev, prevAct, curr);
        assert curr.round() == BettingRound.PREFLOP;
    }
}
