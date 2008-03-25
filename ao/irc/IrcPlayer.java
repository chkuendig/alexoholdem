package ao.irc;

import ao.holdem.v3.engine.Player;
import ao.holdem.v3.engine.RuleBreach;
import ao.holdem.v3.engine.analysis.Analysis;
import ao.holdem.v3.engine.state.State;
import ao.holdem.v3.model.Avatar;
import ao.holdem.v3.model.Chips;
import ao.holdem.v3.model.act.Action;
import ao.holdem.v3.model.card.Community;
import ao.holdem.v3.model.card.Hole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class IrcPlayer implements Player
{
    //--------------------------------------------------------------------
    private List<Action> acts;
    private int          nextAction;


    //--------------------------------------------------------------------
    public void handEnded(Map<Avatar, Chips> deltas) {}

    
    //--------------------------------------------------------------------
    public IrcPlayer(IrcAction action)
    {
        acts       = new ArrayList<Action>();
        nextAction = 0;

        acts.addAll(Arrays.asList( action.preFlop() ));
        acts.addAll(Arrays.asList( action.onFlop()  ));
        acts.addAll(Arrays.asList( action.onTurn()  ));
        acts.addAll(Arrays.asList( action.onRiver() ));
    }


    //--------------------------------------------------------------------
    public Action act(State     state,
                      Hole      hole,
                      Community community,
                      Analysis  analysis)
    {
        if (! nextActionInBounds())
        {
            throw new RuleBreach("unspecified move: " + state);
        }

        return acts.get( nextAction++ );
    }

    public boolean hasQuit()
    {
        boolean isQuit = nextActionInBounds() &&
                         acts.get(nextAction).equals( Action.QUIT );
        if (isQuit) nextAction++;
        return isQuit;
    }


    //--------------------------------------------------------------------
    private boolean nextActionInBounds()
    {
        return acts.size() > nextAction;
    }
}
