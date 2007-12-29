package ao.ai.monte_carlo;

import ao.ai.opp_model.decision.classification.Histogram;
import ao.ai.opp_model.decision.input.raw.example.Context;
import ao.ai.opp_model.input.ModelActionPlayer;
import ao.ai.opp_model.input.ModelHolePlayer;
import ao.ai.opp_model.model.domain.HandStrength;
import ao.holdem.model.act.SimpleAction;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;

/**
 *
 */
public class PredictorService
{
    //--------------------------------------------------------------------
    private HoldemPredictor  actPredictor;
    private HoldemPredictor holePredictor;


    //--------------------------------------------------------------------
    public PredictorService()
    {
        actPredictor =
                new HoldemPredictor(
                        new ModelActionPlayer.Factory());
        holePredictor =
                new HoldemPredictor(
                        new ModelHolePlayer.Factory());
    }


    //--------------------------------------------------------------------
    public void add(HandHistory history)
    {
        actPredictor.add(  history );
        holePredictor.add( history );

//        StateManager start =
//                new StateManager(history.getPlayers(),
//                                 new LiteralCardSource(history));
//        Map<PlayerHandle, ModelActionPlayer> brains =
//                new HashMap<PlayerHandle, ModelActionPlayer>();
//        for (PlayerHandle player : history.getPlayers())
//        {
//            Serializable id = player.getId();
//            brains.put(player,
//                       new ModelActionPlayer(
//                               history,
//                               new PlayerMultiSet(
//                                       actPredictor.classifier(  id),
//                                       holePredictor.classifier( id )),
//                               player,
//                               true));
//        }
//
//        new Dealer(start, brains).playOutHand();
    }


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public Histogram<SimpleAction>
            predictAction(PlayerHandle forPlayer,
                          Context      inContext)
    {
        return (Histogram<SimpleAction>)
                actPredictor.predict(forPlayer, inContext);
    }

    @SuppressWarnings("unchecked")
    public Histogram<HandStrength>
            predictHand(PlayerHandle forPlayer,
                        Context      inContext)
    {
        return (Histogram<HandStrength>)
                holePredictor.predict(forPlayer, inContext);
    }
}
