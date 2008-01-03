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
//        actPredictor.add(  history );
        holePredictor.add( history );
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


    //--------------------------------------------------------------------
    public String toString()
    {
        return "ACTS:\n" + actPredictor.toString() + "\n\n" +
               "HOLES:\n" + holePredictor.toString();
    }
}
