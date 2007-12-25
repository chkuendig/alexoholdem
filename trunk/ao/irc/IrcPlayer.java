package ao.irc;

import ao.holdem.engine.HoldemRuleBreach;
import ao.holdem.model.Player;
import ao.holdem.model.act.RealAction;
import ao.persist.HandHistory;
import ao.state.StateManager;

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
    public void handEnded(HandHistory history) {}

    
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
    public RealAction act(StateManager env)
    {
        if (! nextActionInBounds())
        {
            throw new HoldemRuleBreach("unspecified move: " + env);
        }

        return acts.get( nextAction++ );
    }

    public boolean shiftQuitAction()
    {
        boolean isQuit = nextActionInBounds() &&
                         acts.get(nextAction).equals( RealAction.QUIT );
        if (isQuit) nextAction++;
        return isQuit;
    }


    //--------------------------------------------------------------------
    private boolean nextActionInBounds()
    {
        return acts.size() > nextAction;
    }
}
