package ao.holdem.history.state;

import ao.strategy.Sklansky;
import ao.holdem.model.Hole;
import ao.holdem.model.act.Action;
import ao.holdem.model.act.RealAction;

/**
 *
 */
public class StatePlayerImpl implements StatePlayer
{
    public RealAction act(RunningState env)
    {
        HoldemState state = env.head();
        Hole        hole  = env.cards().holeFor(
                                state.nextToAct().handle() );

        int group = Sklansky.groupOf( hole );

        if (group <= 4)
        {
            return Action.RAISE_OR_CALL.toRealAction(state);
        }
        return Action.CHECK_OR_FOLD.toRealAction(state);
    }
}
