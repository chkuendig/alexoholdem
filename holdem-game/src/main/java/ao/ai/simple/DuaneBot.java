package ao.ai.simple;

import ao.ai.AbstractPlayer;
import ao.ai.simple.starting_hands.Sklansky;
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
 * Strategy from
 *  http://www.pokertips.org/strategy/longhand.php
 */
public class DuaneBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    public void handEnded(Map<Avatar, ChipStack> deltas) {}

    
    //--------------------------------------------------------------------
    public Action act(State        state,
                      CardSequence cards/*,
                      Analysis     analysis*/)
    {
        if (state.round() != Round.PREFLOP) {
            return state.reify(FallbackAction.CHECK_OR_CALL);
        }

        int group = Sklansky.groupOf( cards.hole() );

        FallbackAction act =
                (group <= 5)
                 ? FallbackAction.RAISE_OR_CALL
                 : FallbackAction.CHECK_OR_FOLD;
        return state.reify( act );
    }
}
