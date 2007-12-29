package ao.ai.monte_carlo;

import ao.ai.opp_model.classifier.raw.Classifier;
import ao.ai.opp_model.classifier.raw.DomainedClassifier;
import ao.ai.opp_model.decision.classification.Histogram;
import ao.ai.opp_model.decision.classification.raw.Prediction;
import ao.ai.opp_model.decision.input.raw.example.Context;
import ao.ai.opp_model.decision.random.RandomLearner;
import ao.ai.opp_model.input.InputPlayer;
import ao.ai.opp_model.input.InputPlayerFactory;
import ao.holdem.engine.Dealer;
import ao.holdem.engine.LiteralCardSource;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.state.StateManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * InputPlayer
 */
public class HoldemPredictor
{
    //--------------------------------------------------------------------
    private InputPlayerFactory            examplars;
    private Map<Serializable, Classifier> classifiers;


    //--------------------------------------------------------------------
    public HoldemPredictor(InputPlayerFactory exampleProviders)
    {
        examplars   = exampleProviders;
        classifiers = new HashMap<Serializable, Classifier>();
    }


    //--------------------------------------------------------------------
    public Classifier classifier(Serializable forPlayerId)
    {
        return get(classifiers, forPlayerId);
    }


    //--------------------------------------------------------------------
    public void add(HandHistory history)
    {
        StateManager start =
                new StateManager(history.getPlayers(),
                                 new LiteralCardSource(history));

        Map<PlayerHandle, InputPlayer> brains =
                new HashMap<PlayerHandle, InputPlayer>();
        for (PlayerHandle player : history.getPlayers())
        {
            Classifier learner =
                    get(classifiers, player.getId());
            brains.put(player,
                       examplars.newInstance(
                               history,
                               learner,
                               player,
                               true));
        }

        new Dealer(start, brains).playOutHand();
    }


    //--------------------------------------------------------------------
    private Classifier get(
                Map<Serializable, Classifier> classifiers,
                Serializable                  key)
    {
        Classifier classifier = classifiers.get( key );
        if (classifier == null)
        {
            classifier = new DomainedClassifier( RandomLearner.FACTORY );
            classifiers.put( key, classifier );
        }
        return classifier;
    }


    //--------------------------------------------------------------------
    public Histogram
            predict(PlayerHandle forPlayer,
                    Context      inContext)
    {
        Classifier learner =
                    get(classifiers, forPlayer.getId());
        Prediction p = learner.classify( inContext );
        return p.toHistogram();
    }
}
