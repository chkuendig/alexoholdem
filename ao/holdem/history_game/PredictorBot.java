package ao.holdem.history_game;

import ao.holdem.bots.opp_model.predict.def.context.PredictionContext;
import ao.holdem.bots.opp_model.predict.def.retro.HandParser;
import ao.holdem.bots.util.Util;
import ao.holdem.def.history_bot.HistoryBot;
import ao.holdem.def.model.Money;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.state.action.Action;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.HandHistory;
import ao.holdem.history.PlayerHandle;
import ao.holdem.history.Snapshot;

/**
 *
 */
public class PredictorBot implements HistoryBot
{
    //--------------------------------------------------------------------
    public void introduce() {}
    public void retire()    {}


    //--------------------------------------------------------------------
    public void opponentToAct(
            HandHistory handFromHisPov,
            Snapshot    envFromHisPov)
    {
        HandParser parser = new HandParser();
        PredictionContext ctx =
                parser.nextToActContext(
                        handFromHisPov, envFromHisPov.nextToAct());

//        LearnerSet learners =
//                envFromHisPov.nextToAct().getLearner();
//        PredictorSet predictors =
//                learners.predictors();
//
//        HoldemObservation prediction = predictors.predict( ctx );
//        System.out.println("prediction = " + prediction);
    }

    public void opponentActed(
            PlayerHandle opponent,
            HandHistory  handAfterAction,
            Snapshot     envAfterAction,
            TakenAction  action)
    {
        System.out.println("action = " + action);
    }


    //--------------------------------------------------------------------
    public Action act(HandHistory hand, Snapshot env)
    {
        Hole hole = hand.getHoles().get( env.nextToAct() );
        int group = Util.sklanskyGroup( hole );

        if (group <= 4)
        {
            return Action.RAISE_OR_CALL;
        }
        return Action.CHECK_OR_FOLD;
    }


    //--------------------------------------------------------------------
    public void handStarted()
    {

    }


    //--------------------------------------------------------------------
    public void handEnded(HandHistory atEndOfHand,
                          Money       stackDelta)
    {
//        HandParser parser = new HandParser();
//
//        for (PlayerHandle p : atEndOfHand.getPlayers())
//        {
//            HoldemRetroSet cases =
//                    parser.casesFor(atEndOfHand, p);
//
//            LearnerSet learner = p.getLearner();
//
//            cases.train( learner, 100, 1000 );
//        }
    }
}
