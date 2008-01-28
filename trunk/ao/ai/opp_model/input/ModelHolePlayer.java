package ao.ai.opp_model.input;

import ao.ai.opp_model.classifier.raw.Classifier;
import ao.ai.opp_model.classifier.raw.Predictor;
import ao.ai.opp_model.decision.input.raw.example.Context;
import ao.ai.opp_model.decision.input.raw.example.Datum;
import ao.ai.opp_model.decision.input.raw.example.Example;
import ao.ai.opp_model.model.domain.HandStrength;
import ao.holdem.model.act.RealAction;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.odds.agglom.BlindOddFinder;
import ao.odds.agglom.impl.ApproxBlindOddFinder;
import ao.holdem.engine.persist.HandHistory;
import ao.holdem.engine.persist.PlayerHandle;
import ao.holdem.engine.state.HandState;
import ao.holdem.engine.state.StateManager;

/**
 * 
 */
public class ModelHolePlayer extends LearningPlayer
{
    //--------------------------------------------------------------------
    public static class Factory
                            implements LearningPlayer.Factory
    {
        public LearningPlayer newInstance(
                boolean      publishActions,
                HandHistory  history,
                PlayerHandle player,
                Classifier   addTo,
                Predictor predictWith)
        {
            return new ModelHolePlayer(
                    publishActions, history, player, addTo, predictWith);
        }
    }


    //--------------------------------------------------------------------
    private BlindOddFinder expectedOdds = new ApproxBlindOddFinder();


    //--------------------------------------------------------------------
    public ModelHolePlayer(
            boolean      publishActions,
            HandHistory  history,
            PlayerHandle player,
            Classifier   addTo,
            Predictor    predictWith)
    {
        super(publishActions, history, player, addTo, predictWith);
    }


    //--------------------------------------------------------------------
    protected Example makeExampleOf(
            StateManager env,
            Context      ctx,
            RealAction   action)
    {
        if (! env.holeVisible( env.nextToAct() )) return null;

        HandState state = env.head();
        Hole      hole  = env.cards().holeFor(
                                state.nextToAct().handle() );

        // showdowns only
        if (!(actsLeft() == 0 && !action.isFold())) return null;

        Community community = env.cards().community();

        HandStrength actualDelta =
                HandStrength.fromState(state, community, hole);

//        Prediction prediction = predict(ctx);
//        if (prediction != null)
//        {
////            System.out.println(playerId()                + "\t" +
////                               ctx.data().size() + "\t" +
////                               prediction                + "\t" +
////                               actualDelta);
//        }
        return ctx.withTarget(new Datum( actualDelta ));
    }
}

