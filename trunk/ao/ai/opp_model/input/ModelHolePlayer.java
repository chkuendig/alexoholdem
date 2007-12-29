package ao.ai.opp_model.input;

import ao.ai.opp_model.classifier.raw.Classifier;
import ao.ai.opp_model.decision.input.raw.example.Context;
import ao.ai.opp_model.decision.input.raw.example.Datum;
import ao.ai.opp_model.decision.input.raw.example.Example;
import ao.ai.opp_model.model.domain.HandStrength;
import ao.holdem.model.act.RealAction;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.odds.*;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.state.HandState;
import ao.state.StateManager;

/**
 * 
 */
public class ModelHolePlayer extends InputPlayer
{
    //--------------------------------------------------------------------
    public static class Factory
                            implements InputPlayerFactory
    {
        public InputPlayer newInstance(
                HandHistory  history,
                Classifier   addTo,
                PlayerHandle player,
                boolean      publishActions)
        {
            return new ModelHolePlayer(
                            history, addTo, player, publishActions);
        }
    }


    //--------------------------------------------------------------------
    public ModelHolePlayer(
            HandHistory  history,
            Classifier   addTo,
            PlayerHandle player,
            boolean      publishActions)
    {
        super(history, addTo, player, publishActions);
    }


    //--------------------------------------------------------------------
    protected Example makeExampleOf(
            StateManager env,
            Context      ctx,
            RealAction   act)
    {
        HandState state = env.head();

        Hole hole = env.cards().holeFor(
                        state.nextToAct().handle() );
        if (hole == null || !hole.bothCardsVisible()) return null;

        Community community = env.cards().community();

        OddFinder oddFinder = new ApproximateOddFinder();
        Odds      actual    =
                    oddFinder.compute(
                            hole, community, state.numActivePlayers()-1);

        BlindOddFinder expectationFinder = new ApproxBlindOddFinder();
        Odds           expected          =
                            expectationFinder.compute(
                                    community, state.numActivePlayers());

        HandStrength actualDelta =
                HandStrength.fromPercent(
                                    actual.strengthVsRandom() -
                                        expected.strengthVsRandom());
        System.out.println(predict(ctx) + "\t" + actualDelta);
        return ctx.withTarget(new Datum( actualDelta ));
    }
}

