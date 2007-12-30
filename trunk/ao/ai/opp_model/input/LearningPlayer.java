package ao.ai.opp_model.input;

import ao.ai.opp_model.classifier.raw.Classifier;
import ao.ai.opp_model.decision.classification.ConfusionMatrix;
import ao.ai.opp_model.decision.classification.raw.Prediction;
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
public abstract class LearningPlayer
        implements Player
{
    //--------------------------------------------------------------------
    public static interface Factory
    {
        public LearningPlayer newInstance(
                                HandHistory  history,
                                Classifier   addTo,
                                PlayerHandle player,
                                boolean      publishActions);
    }


    //--------------------------------------------------------------------
    private LinkedList<RealAction> acts;
    private Classifier             examples;
    private Serializable           playerId;
    private boolean                publish;

    private ConfusionMatrix confusion = new ConfusionMatrix();


    //--------------------------------------------------------------------
    public LearningPlayer(HandHistory  history,
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

    protected Serializable playerId()
    {
        return playerId;
    }


    //--------------------------------------------------------------------
    public void handEnded(HandHistory history) {}


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public RealAction act(final StateManager env)
    {
        checkPlayer(env);

        final RealAction act = shiftAction();
        if (publish && !act.isBlind())
        {
            Statistic stat = env.stats().forPlayer(playerId);

            Context ctx    = stat.nextActContext();
            Example addend = makeExampleOf(env, ctx, act);

            if (addend != null)
            {
                // record prediction accuracy
                confusion.add(
                        addend.target().state(),
                        predict(ctx).toHistogram().mostFrequent());

                examples.add( addend );
            }
        }
        return act;
    }

    protected abstract Example
            makeExampleOf(StateManager env,
                          Context      ctx,
                          RealAction   act);


    //--------------------------------------------------------------------
    protected Prediction predict(Context ctx)
    {
        return examples.classify(ctx);
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


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void addTo(ConfusionMatrix confusionMatrix)
    {
        confusionMatrix.addAll( confusion );
    }
    
    public String toString()
    {
        return "confusion for player: " + playerId + "\n" +
                confusion.toString();
    }
}
