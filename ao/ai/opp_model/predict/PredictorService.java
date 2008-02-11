package ao.ai.opp_model.predict;

import ao.ai.supervised.decision.classification.RealHistogram;
import ao.ai.supervised.decision.input.raw.example.Context;
import ao.ai.opp_model.input.ModelActionPlayer;
import ao.ai.opp_model.predict.hole.DeltaApprox;
import ao.holdem.model.act.SimpleAction;
import ao.holdem.engine.persist.HandHistory;
import ao.holdem.engine.persist.PlayerHandle;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class PredictorService
{
    //--------------------------------------------------------------------
    private HoldemPredictor<SimpleAction> actPredictor;
    private DeltaApprox handPredictor;


    //--------------------------------------------------------------------
    public PredictorService()
    {
        actPredictor =
                new HoldemPredictor<SimpleAction>(
                        new ModelActionPlayer.Factory());
        handPredictor = new DeltaApprox( actPredictor );
    }


    //--------------------------------------------------------------------
    public void add(HandHistory history)
    {
        actPredictor.add(    history );
        handPredictor.learn( history );
        //holePredictor.add( history );
    }


    //--------------------------------------------------------------------
    public RealHistogram<SimpleAction>
            predictAction(PlayerHandle forPlayer,
                          Context      inContext)
    {
        return actPredictor.predict(forPlayer, inContext);
    }

//    public RealHistogram<HandStrength>
//            predictHand(PlayerHandle forPlayer,
//                        List<Choice> fromChoices)
//    {
//        return handPredictor.approximate( forPlayer, fromChoices );
//    }
//    public RealHistogram<PlayerHandle> approximate(HandHistory history)
//    {
//        return handPredictor.approximateShowdown( history );
//    }
    public RealHistogram<PlayerHandle>
            approximate( Map<PlayerHandle, List<Choice>> choices )
    {
        return handPredictor.approximate( choices );
    }


    //--------------------------------------------------------------------
    public void examine(HandHistory history)
    {
        handPredictor.examine( history );
    }

    public Map<PlayerHandle, List<Choice>>
            extractChoices(HandHistory history)
    {
        return extractChoices(history, null);
    }
    public Map<PlayerHandle, List<Choice>>
            extractChoices(HandHistory history, PlayerHandle onlyFor)
    {
        return handPredictor.extractChoices(history, onlyFor, false);
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return "ACTS:\n" + actPredictor.toString() + "\n\n" +
               "HOLES:\n" + handPredictor.toString();
    }
}
