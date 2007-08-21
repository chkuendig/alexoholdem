package ao.holdem.bots.opp_model;

import ao.holdem.bots.opp_model.mix.MixedAction;
import ao.holdem.bots.opp_model.predict.BackpropPredictor;
import ao.holdem.bots.opp_model.predict.PredictionSet;
import ao.holdem.bots.opp_model.predict.def.observation.Observation;
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
        modelOpponet(playerAccess.find("irc", "timlo"));
//        backprop(playerAccess.find("irc", "Barrister"));
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
        LearnerSet learners = new LearnerSet();

        HoldemRetroSet trainRetros = new HoldemRetroSet();
        HoldemRetroSet validRetros = new HoldemRetroSet();

        int i = 0;
        for (HandHistory hand : p.getHands())
        {
            HoldemRetroSet retros = hand.casesFor(p);

            if (i++ < 10000) {
                trainRetros.add( retros );
            } else {
                validRetros.add( retros );
            }
        }
        trainRetros.train(learners, 2000, 20000000);
        PredictorSet predictors = learners.predictors();
        for (Retrodiction<?> retro : trainRetros.holeBlind().cases())
        {
            Observation prediction = predictors.predict(retro);

            System.out.println(retro.predictionType() + "\t" +
                               prediction + "\t" +
                               new MixedAction(retro.neuralOutput()));
        }

//        for (HandHistory hand : p.getHands())
//        {
////            System.out.println("====\t" + (++count));
//            HoldemRetroSet retros     = hand.casesFor(p);
//            PredictorSet predictors = learners.predictors();
//
//            for (Retrodiction<?> retro : retros.holeBlind().cases())
//            {
//                Observation prediction = predictors.predict(retro);
//
//                System.out.println(retro.predictionType() + "\t" +
//                                   prediction + "\t" +
//                                   new MixedAction(retro.neuralOutput()));
//            }
//
//            retros.train(learners, 100, 100000);
////            allRetros.add( retros );
//        }
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
