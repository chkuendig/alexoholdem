package ao.ai.opp_model.input;

import ao.ai.opp_model.decision.data.DataPool;
import ao.ai.opp_model.model.context.PlayerExampleSet;
import ao.ai.opp_model.model.data.DomainedExample;
import ao.ai.opp_model.model.data.HoldemContext;
import ao.holdem.model.Money;
import ao.holdem.model.Player;
import ao.holdem.model.act.RealAction;
import ao.persist.Event;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.state.StateManager;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 */
public abstract class InputPlayer
        implements Player
{
    //--------------------------------------------------------------------
    private LinkedList<RealAction> acts;
    private PlayerExampleSet       examples;
    private Serializable           playerId;
    private DataPool               pool;
    private boolean                publish;


    //--------------------------------------------------------------------
    public InputPlayer(HandHistory      history,
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
        if (publish && !act.isBlind())
        {
            HoldemContext ctx =
                env.stats().forPlayer(playerId)
                        .nextActContext(pool);
            examples.add(makeExampleOf(env, ctx, act, pool));
        }
        return act;
    }

    protected abstract DomainedExample
            makeExampleOf(StateManager  env,
                          HoldemContext ctx,
                          RealAction    act,
                          DataPool      pool);

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
