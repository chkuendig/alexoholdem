package ao.holdem.history_game;

import ao.holdem.bots.opp_model.predict.def.retro.HandParser;
import ao.holdem.bots.opp_model.predict.def.retro.HoldemRetroSet;
import ao.holdem.bots.opp_model.predict.def.retro.LearnerSet;
import ao.holdem.def.history_bot.HistoryBot;
import ao.holdem.def.model.Money;
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
            Snapshot    envFromHisPov) {}

    public void opponentActed(
            HandHistory handAfterAction,
            Snapshot    envAfterAction,
            TakenAction action) {}


    //--------------------------------------------------------------------
    public Action act(HandHistory hand, Snapshot env)
    {
        return null;
    }


    //--------------------------------------------------------------------
    public void handEnded(HandHistory atEndOfHand,
                          Money       stackDelta)
    {
        HandParser parser = new HandParser();

        for (PlayerHandle p : atEndOfHand.getPlayers())
        {
            HoldemRetroSet cases =
                    parser.casesFor(atEndOfHand, p);

            LearnerSet learner = p.getLearner();

            cases.train( learner, 100, 1000 );
//            predictor.
            
//            predictor.
        }
    }
}
