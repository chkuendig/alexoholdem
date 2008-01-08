package ao.ai.opp_model.input;

import ao.ai.opp_model.decision.input.raw.example.Context;
import ao.holdem.model.Player;
import ao.holdem.model.act.RealAction;
import ao.persist.Event;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.state.StateManager;
import ao.stats.Statistic;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

/**
 *
 */
public class ContextPlayer implements Player
{
    //--------------------------------------------------------------------
    private LinkedList<RealAction> acts;
    private Serializable           playerId;
    private List<Context>          contexts;
    private boolean                reachedShowdown;


    //--------------------------------------------------------------------
    public ContextPlayer(HandHistory  history,
                         PlayerHandle player)
    {
        acts     = new LinkedList<RealAction>();
        contexts = new ArrayList<Context>();
        playerId = player.getId();

        for (Event event : history.getEvents( player ))
        {
            acts.add( event.getAction() );
        }
    }


    //--------------------------------------------------------------------
    public void handEnded(HandHistory history) {}


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public RealAction act(final StateManager env)
    {
        checkPlayer(env);

        final RealAction act = shiftAction();
        if (!act.isBlind())
        {
            Statistic stat = env.stats().forPlayer(playerId);

            Context ctx = stat.nextActContext();
            contexts.add( ctx );

            if (acts.isEmpty() && !act.isFold())
            {
                reachedShowdown = true;
            }
        }
        return act;
    }


    //--------------------------------------------------------------------
    public List<Context> contexts()
    {
        return contexts;
    }
    
    public boolean reachedShowdown()
    {
        return reachedShowdown;
    }


    //--------------------------------------------------------------------
    public boolean shiftQuitAction()
    {
        boolean isQuit = !acts.isEmpty() &&
                          acts.getFirst().equals( RealAction.QUIT );
        if (isQuit) shiftAction();
        return isQuit;
    }


    //--------------------------------------------------------------------
    private void checkPlayer(StateManager env)
    {
        assert playerId.equals( env.nextToAct().getId() );
    }
    private RealAction shiftAction()
    {
        return acts.removeFirst();
    }
}
