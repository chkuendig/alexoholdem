package ao.holdem.ai.ai.simple;

import ao.ai.AbstractPlayer;
import ao.holdem.engine.state.State;
import ao.holdem.model.Avatar;
import ao.holdem.model.ChipStack;
import ao.holdem.model.Round;
import ao.holdem.model.act.Action;
import ao.holdem.model.act.FallbackAction;
import ao.holdem.model.card.sequence.CardSequence;

import java.util.Map;

//import ao.holdem.engine.analysis.Analysis;

/**
 * User: alex
 * Date: 27-Apr-2009
 * Time: 12:24:37 PM
 */
public class RaiseCallBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    public void handEnded(Map<Avatar, ChipStack> deltas) {}


    //--------------------------------------------------------------------
    public Action act(State        state,
                      CardSequence cards/*,
                      Analysis     analysis*/)
    {
        return (state.round().ordinal() < Round.RIVER.ordinal() ||
                state.remainingBetsInRound() > 3)
               ? state.reify(FallbackAction.RAISE_OR_CALL)
               : state.reify(FallbackAction.CHECK_OR_CALL);
    }
}
