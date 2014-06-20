package ao.holdem.bot.simple;

import ao.holdem.bot.AbstractPlayer;
import ao.holdem.engine.state.ActionState;
import ao.holdem.model.act.Action;
import ao.holdem.model.act.FallbackAction;
import ao.holdem.model.card.sequence.CardSequence;

//import ao.holdem.engine.analysis.Analysis;

/**
 * User: alex
 * Date: 3-Apr-2009
 * Time: 4:57:35 PM
 */
public class AlwaysCallBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    public Action act(ActionState state,
                      CardSequence cards/*,
                      Analysis analysis*/)
    {
        return state.reify(FallbackAction.CHECK_OR_CALL);
    }
}
