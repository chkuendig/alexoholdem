package ao.ai.opp_model.input;

import ao.ai.opp_model.decision.data.DataPool;
import ao.ai.opp_model.model.context.PlayerExampleSet;
import ao.ai.opp_model.model.data.DomainedExample;
import ao.ai.opp_model.model.data.HoldemContext;
import ao.ai.opp_model.model.data.HoldemExample;
import ao.ai.opp_model.model.domain.HandStrengthDelta;
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
    public ModelHolePlayer(
            HandHistory      history,
            PlayerExampleSet addTo,
            PlayerHandle     player,
            DataPool         attributePool,
            boolean          publishActions)
    {
        super(history, addTo, player, attributePool, publishActions);
    }


    //--------------------------------------------------------------------
    protected DomainedExample makeExampleOf(
                                StateManager  env,
                                HoldemContext ctx,
                                RealAction    act,
                                DataPool      pool)
    {
        HandState state = env.head();

        Hole hole = env.cards().holeFor(
                        state.nextToAct().handle() );

        Community community = env.cards().community();

        OddFinder oddFinder = new ApproximateOddFinder();
        Odds      actual    =
                    oddFinder.compute(
                            hole, community, state.numActivePlayers()-1);

        BlindOddFinder expectationFinder = new ApproxBlindOddFinder();
        Odds           expected          =
                            expectationFinder.compute(
                                    community, state.numActivePlayers());

        return new HoldemExample(
                        ctx,
                        pool.fromEnum(
                                HandStrengthDelta.fromPercent(
                                    actual.strengthVsRandom() -
                                        expected.strengthVsRandom())));
    }
}

