package ao.ai.opp_model;

import ao.holdem.model.Player;
import ao.holdem.model.act.RealAction;
import ao.persist.Event;
import ao.persist.HandHistory;
import ao.state.CumulativeState;
import ao.state.StateManager;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * A player that extracts action examples from a HandHistory
 */
public class ModelPlayer implements Player
{
    //--------------------------------------------------------------------
    private LinkedList<RealAction> acts;
    private HandHistory            hist;
    private CumulativeState        modelDeleget;
    private Serializable           playerId;


    //--------------------------------------------------------------------
    public ModelPlayer(HandHistory history, CumulativeState model)
    {
        hist         = history;
        acts         = new LinkedList<RealAction>();
        modelDeleget = model;
    }


    //--------------------------------------------------------------------
    public RealAction act(StateManager env)
    {
        if   (initNeeded()) init(env);
        else                checkPlayer(env);

        RealAction act = nextAction();
        modelDeleget.advance(env.head(),
                             env.head().nextToAct(),
                             act,
                             env.cards().community());
        return act;
    }


    //--------------------------------------------------------------------
    private boolean initNeeded()
    {
        return hist == null;
    }
    private void init(StateManager env)
    {
        for (Event event : hist.getEvents( env.nextToAct() ))
        {
            acts.add( event.getAction() );
        }
        hist     = null;
        playerId = env.nextToAct().getId();
    }
    private void checkPlayer(StateManager env)
    {
        assert playerId.equals( env.nextToAct().getId() );
    }
    public RealAction nextAction()
    {
        return acts.removeFirst();
    }
}
