package ao.ai.opp_model.input;

import ao.ai.opp_model.classifier.raw.Classifier;
import ao.ai.opp_model.decision.input.raw.example.Context;
import ao.ai.opp_model.decision.input.raw.example.Example;
import ao.holdem.model.Player;
import ao.holdem.model.act.RealAction;
import ao.persist.Event;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.state.StateManager;
import ao.stats.Statistic;

import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 */
public abstract class InputPlayer
        implements Player
{
    //--------------------------------------------------------------------
    private LinkedList<RealAction> acts;
    private Classifier             examples;
    private Serializable           playerId;
    private boolean                publish;


    //--------------------------------------------------------------------
    public InputPlayer(HandHistory  history,
                       Classifier   addTo,
                       PlayerHandle player,
                       boolean      publishActions)
    {
        acts     = new LinkedList<RealAction>();
        playerId = player.getId();
        examples = addTo;
        publish  = publishActions;

        for (Event event : history.getEvents( player ))
        {
            acts.add( event.getAction() );
        }
    }


    //--------------------------------------------------------------------
    public void handEnded(HandHistory history) {}


    //--------------------------------------------------------------------
    public RealAction act(final StateManager env)
    {
        checkPlayer(env);

        final RealAction act = shiftAction();
        if (publish && !act.isBlind())
        {
            Statistic stat = env.stats().forPlayer(playerId);

            Example addend =
                    makeExampleOf(env,
                                  stat.nextActContext(),
                                  act);
            //XXX: add checking here.
            //examples.classify( addend );
            examples.add( addend );
        }
        return act;
    }

    protected abstract Example
            makeExampleOf(StateManager env,
                          Context      ctx,
                          RealAction   act);


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
