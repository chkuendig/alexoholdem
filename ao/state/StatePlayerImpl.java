package ao.state;

import ao.holdem.history.state.HoldemState;
import ao.holdem.model.Hole;
import ao.holdem.model.Player;
import ao.holdem.model.act.EasyAction;
import ao.holdem.model.act.RealAction;
import ao.strategy.Sklansky;

/**
 *
 */
public class StatePlayerImpl implements Player
{
    public RealAction act(Context env)
    {
        HoldemState state = env.head();
        Hole        hole  = env.cards().holeFor(
                                state.nextToAct().handle() );

        int group = Sklansky.groupOf( hole );

        if (group <= 4)
        {
            return EasyAction.RAISE_OR_CALL.toRealAction(state);
        }
        return EasyAction.CHECK_OR_FOLD.toRealAction(state);
    }
}
