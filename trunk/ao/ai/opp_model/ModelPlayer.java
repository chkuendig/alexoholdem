package ao.ai.opp_model;

import ao.ai.opp_model.model.context.PlayerExampleSet;
import ao.ai.opp_model.model.data.ActionExample;
import ao.ai.opp_model.model.data.HoldemContext;
import ao.ai.opp_model.decision2.data.DataPool;
import ao.holdem.model.Player;
import ao.holdem.model.Money;
import ao.holdem.model.act.RealAction;
import ao.persist.Event;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.state.StateManager;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map;

/**
 * A player that extracts action examples from a HandHistory
 */
public class ModelPlayer implements Player
{
    //--------------------------------------------------------------------
    private LinkedList<RealAction> acts;
    private PlayerExampleSet       examples;
    private Serializable           playerId;
    private DataPool               pool;
    private boolean                publish;


    //--------------------------------------------------------------------
    public ModelPlayer(HandHistory      history,
                       PlayerExampleSet addTo,
                       PlayerHandle     player,
                       DataPool         attributePool,
                       boolean          publishActions)
    {
        acts     = new LinkedList<RealAction>();
        playerId = player.getId();
        examples = addTo;
        pool     = attributePool;
        publish  = publishActions;

        for (Event event : history.getEvents( player ))
        {
            acts.add( event.getAction() );
        }
    }


    //--------------------------------------------------------------------
    public void handEnded(Map<PlayerHandle, Money> deltas) {}

    
    //--------------------------------------------------------------------
    public RealAction act(StateManager env)
    {
        checkPlayer(env);

        RealAction act = shiftAction();
        if (! act.isBlind() && publish)
        {
            HoldemContext ctx =
                    env.stats().forPlayer(playerId).nextActContext(pool);
//            System.out.println(ctx);
            examples.add(new ActionExample(
                            ctx,
                            pool.fromEnum(act.toSimpleAction())));
        }
        return act;
    }

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
