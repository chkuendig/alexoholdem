package ao.holdem.history.irc;

import ao.holdem.model.act.RealAction;
import ao.state.Context;
import ao.holdem.model.Player;
import ao.holdem.history.state.HoldemRuleBreach;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class IrcPlayer implements Player
{
    //--------------------------------------------------------------------
    private List<RealAction> acts;
    private int              nextAction;


    //--------------------------------------------------------------------
    public IrcPlayer(IrcAction action)
    {
        acts       = new ArrayList<RealAction>();
        nextAction = 0;

        acts.addAll(Arrays.asList( action.preFlop() ));
        acts.addAll(Arrays.asList( action.onFlop()  ));
        acts.addAll(Arrays.asList( action.onTurn()  ));
        acts.addAll(Arrays.asList( action.onRiver() ));
    }


    //--------------------------------------------------------------------
    public RealAction act(Context env)
    {
        if (acts.size() <= nextAction)
        {
            throw new HoldemRuleBreach("unexpected move on " + env);
        }

        return acts.get( nextAction++ );
    }
}
