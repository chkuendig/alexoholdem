package ao.holdem.bots.hist;

import ao.decision.context.HoldemContext;
import ao.decision.domain.HoldemHandParser;
import ao.holdem.bots.opp_model.decision.ModelPool;
import ao.holdem.bots.opp_model.mix.MixedAction;
import ao.holdem.bots.util.Util;
import ao.holdem.def.history_bot.HistoryBot;
import ao.holdem.def.model.Money;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.state.action.Action;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.HandHistory;
import ao.holdem.history.PlayerHandle;
import ao.holdem.history.Snapshot;
import ao.holdem.history.state.HoldemState;

/**
 *
 */
public class PredictorBot implements HistoryBot
{
    //--------------------------------------------------------------------
    private ModelPool        model         = new ModelPool();
    private HoldemHandParser decisionSetup = new HoldemHandParser();


    //--------------------------------------------------------------------
    public void introduce() {}
    public void retire()    {}


    //--------------------------------------------------------------------
    public void opponentToAct(
            HandHistory handFromHisPov,
            Snapshot    envFromHisPov)
    {
        PlayerHandle  nextToAct = envFromHisPov.nextToAct();
        HoldemContext ctx =
                decisionSetup.nextToActContext(
                        handFromHisPov, nextToAct);
        MixedAction prediction = model.predict(nextToAct, ctx);

        System.out.println("prediction = " + prediction);
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
    public Action act(HandHistory hand, HoldemState env)
    {
        Hole hole = hand.getHoles().get( env.nextToAct().handle() );
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
        model.add( atEndOfHand );
    }
}
