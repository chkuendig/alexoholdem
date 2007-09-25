package ao.ai.opp_model;

import ao.ai.opp_model.decision.attr.AttributePool;
import ao.ai.opp_model.decision.context.PlayerExampleSet;
import ao.ai.opp_model.decision.data.ActionExample;
import ao.holdem.model.Player;
import ao.holdem.model.act.RealAction;
import ao.persist.Event;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
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
    private PlayerExampleSet examples;
    private Serializable           playerId;
    private AttributePool          pool;


    //--------------------------------------------------------------------
    public ModelPlayer(HandHistory      history,
                       PlayerExampleSet addTo,
                       PlayerHandle     player,
                       AttributePool    attributePool)
    {
        acts     = new LinkedList<RealAction>();
        playerId = player.getId();
        examples = addTo;
        pool     = attributePool;

        for (Event event : history.getEvents( player ))
        {
            acts.add( event.getAction() );
        }
    }


    //--------------------------------------------------------------------
    public RealAction act(StateManager env)
    {
        checkPlayer(env);

        RealAction act = nextAction();
        examples.add(new ActionExample(
                            env.stats().forPlayer(playerId).stats(pool),
                            pool.fromEnum(act.toSimpleAction())));
        return act;
    }


    //--------------------------------------------------------------------
    private void checkPlayer(StateManager env)
    {
        assert playerId.equals( env.nextToAct().getId() );
    }
    public RealAction nextAction()
    {
        return acts.removeFirst();
    }
}
