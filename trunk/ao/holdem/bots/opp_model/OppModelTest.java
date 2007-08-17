package ao.holdem.bots.opp_model;

import ao.holdem.bots.opp_model.mix.MixedAction;
import ao.holdem.bots.opp_model.predict.BackpropPredictor;
import ao.holdem.bots.opp_model.predict.PredictionSet;
import ao.holdem.bots.opp_model.predict.def.observation.HoldemObservation;
import ao.holdem.bots.opp_model.predict.def.retro.HoldemRetroSet;
import ao.holdem.bots.opp_model.predict.def.retro.LearnerSet;
import ao.holdem.bots.opp_model.predict.def.retro.PredictorSet;
import ao.holdem.bots.opp_model.predict.def.retro.Retrodiction;
import ao.holdem.history.HandHistory;
import ao.holdem.history.PlayerHandle;
import ao.holdem.history.persist.PlayerHandleAccess;
import com.google.inject.Inject;
import com.wideplay.warp.persist.Transactional;

/**
 *
 */
public class OppModelTest
{
    //--------------------------------------------------------------------
    @Inject PlayerHandleAccess playerAccess;


    //--------------------------------------------------------------------
    @Transactional
    public void testOpponentModeling()
    {
//        retrieveMostPrevalent();
        modelOpponet(playerAccess.find("irc", "doc_doc"));
//        backprop(playerAccess.find("irc", "doc_doc"));
    }


    //--------------------------------------------------------------------
    private void modelOpponet(PlayerHandle p)
    {
        System.out.println("analyzing " + p.getName());

        try
        {
            doModelOpponet( p );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void doModelOpponet(PlayerHandle p) throws Exception
    {
//        HoldemRetroSet allRetros = new HoldemRetroSet();

        LearnerSet learners = new LearnerSet();
        for (HandHistory hand : p.getHands())
        {
//            System.out.println("====\t" + (++count));
            HoldemRetroSet retros     = hand.casesFor(p);
            PredictorSet   predictors = learners.predictors();

            for (Retrodiction<?> retro : retros.holeBlind().cases())
            {
                HoldemObservation prediction =
                        predictors.predict(retro);

                System.out.println(retro.predictionType() + "\t" +
                                   prediction + "\t" +
                                   new MixedAction(retro.neuralOutput()));
            }

            retros.train(learners, 1000, 1000000);
//            allRetros.add( retros );
        }
    }

    // see http://www.jooneworld.com/docs/sampleEngine.html
    private void backprop(PlayerHandle p)
    {
        PredictionSet predictions = new PredictionSet();
        predictions.addPlayerHands(p);
        new BackpropPredictor().trainOn( predictions );
        System.out.println("at end of backprop");
    }


    //--------------------------------------------------------------------
    /*  sagerbot :: 2785
        gsr :: 2515
        oejack4 :: 2293
        Shazbot :: 1934
        alfalfa :: 1861
        pliu :: 1650
        num :: 1596
        jb :: 1586
        bmk :: 1470
        perfecdoh :: 1461 */
    private void retrieveMostPrevalent()
    {
        for (PlayerHandle p : playerAccess.byPrevalence(200))
        {
            System.out.println(
                    p.getName() + " :: " + p.getHands().size());
        }
    }
}
