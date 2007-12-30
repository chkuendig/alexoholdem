package ao.ai.monte_carlo;

import ao.ai.opp_model.classifier.raw.Classifier;
import ao.ai.opp_model.classifier.raw.DomainedClassifier;
import ao.ai.opp_model.decision.classification.ConfusionMatrix;
import ao.ai.opp_model.decision.classification.Histogram;
import ao.ai.opp_model.decision.classification.raw.Prediction;
import ao.ai.opp_model.decision.input.raw.example.Context;
import ao.ai.opp_model.decision.random.RandomLearner;
import ao.ai.opp_model.input.LearningPlayer;
import ao.holdem.engine.Dealer;
import ao.holdem.engine.LiteralCardSource;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.state.StateManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * InputPlayer
 */
public class HoldemPredictor
{
    //--------------------------------------------------------------------
    private static final int MEMORY = 32;


    //--------------------------------------------------------------------
    private LearningPlayer.Factory                  examplars;
    private LinkedHashMap<Serializable, Classifier> classifiers;
    private Map<Serializable, ConfusionMatrix>      errors;
    private ConfusionMatrix                         otherErrs;


    //--------------------------------------------------------------------
    public HoldemPredictor(LearningPlayer.Factory exampleProviders)
    {
        examplars   = exampleProviders;
        otherErrs   = new ConfusionMatrix();
        errors      = new HashMap<Serializable, ConfusionMatrix>();
        classifiers = new LinkedHashMap<Serializable, Classifier>(
                                MEMORY, 0.75f, true);
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

        Map<PlayerHandle, LearningPlayer> brains =
                new HashMap<PlayerHandle, LearningPlayer>();
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

        for (Map.Entry<PlayerHandle, LearningPlayer> brain :
                brains.entrySet())
        {
            ConfusionMatrix err = errors.get( brain.getKey().getId() );
            if (err == null)
            {
                err = new ConfusionMatrix();
                errors.put( brain.getKey().getId(), err );
            }
            brain.getValue().addTo( err );
        }
    }


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private synchronized Classifier get(
                LinkedHashMap<Serializable, Classifier> classifiers,
                Serializable                            key)
    {
        Classifier classifier = classifiers.get( key );
        if (classifier == null)
        {
            classifier = new DomainedClassifier(
                                new RandomLearner.Factory() );
            classifiers.put( key, classifier );

            while (classifiers.size() > MEMORY)
            {
                Serializable forgottenId =
                        classifiers.keySet().iterator().next();
                classifiers.remove( forgottenId );
                otherErrs.addAll( errors.remove(forgottenId) );
            }
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


    //--------------------------------------------------------------------
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        for (Map.Entry<Serializable, ConfusionMatrix> err :
                errors.entrySet())
        {
            str.append("\nConfusion for ")
               .append(err.getKey())
               .append("\n")
               .append(err.getValue().toString());
        }

        str.append("Confusion for others\n")
               .append(otherErrs.toString());
        return str.toString();
    }
}
