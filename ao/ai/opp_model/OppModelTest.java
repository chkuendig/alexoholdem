package ao.ai.opp_model;

import ao.ai.opp_model.decision.DecisionLearner;
import ao.ai.opp_model.decision.data.DataSet;
import ao.ai.opp_model.decision.data.Example;
import ao.ai.opp_model.decision.data.Histogram;
import ao.ai.opp_model.decision.domain.HoldemHandParser;
import ao.ai.opp_model.decision.tree.DecisionTreeLearner;
import ao.ai.opp_model.mix.MixedAction;
import ao.ai.opp_model.neural.BackpropPredictor;
import ao.ai.opp_model.neural.PredictionSet;
import ao.ai.opp_model.neural.def.observation.Observation;
import ao.ai.opp_model.neural.def.retro.HoldemRetroSet;
import ao.ai.opp_model.neural.def.retro.LearnerSet;
import ao.ai.opp_model.neural.def.retro.PredictorSet;
import ao.ai.opp_model.neural.def.retro.Retrodiction;
import ao.holdem.model.act.SimpleAction;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.persist.dao.PlayerHandleAccess;
import com.google.inject.Inject;
import com.wideplay.warp.persist.Transactional;

import java.util.List;

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
        modelOpponet(playerAccess.find("irc", "sagerbot"));
//        backprop(playerAccess.find("irc", "Barrister"));
    }


    //--------------------------------------------------------------------
    private void modelOpponet(PlayerHandle p)
    {
        System.out.println("analyzing " + p.getName());

        try
        {
//            doModelOpponet( p );
            doDecisionModelOpponet( p );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void doDecisionModelOpponet(PlayerHandle p)
    {
        HoldemHandParser decisionSetup = new HoldemHandParser();

        DataSet<SimpleAction> trainingSet   = new DataSet<SimpleAction>();
        DataSet<SimpleAction> validationSet = new DataSet<SimpleAction>();

        int i = 0;
        for (HandHistory hand : p.getHands())
        {
            List<Example<SimpleAction>> handExamples =
                    decisionSetup.postflopExamples(hand, p);

            if (i++ < 2000) {
                trainingSet.addAll( handExamples );
            } else {
                validationSet.addAll( handExamples );
            }
        }

        System.out.println("building model");
        DecisionLearner<SimpleAction> learner =
                new DecisionTreeLearner<SimpleAction>();
        learner.train( trainingSet );
        
        for (Example<SimpleAction> example : validationSet.examples())
        {
            trainingSet.add( example );
//            if (Rand.nextDouble() < (1/20.0))
//            {
//                learner.train( trainingSet );
//            }
            
            Histogram<SimpleAction> prediction =
                    learner.predict( example );
            System.out.println(
                    example.target().value() + "\t" +
                    prediction + "\t" +
                    prediction.probabilityOf(
                            example.target().value()) + "\t" +
                    (prediction.mostProbable().equals(
                            example.target()) ? 1 : 0));
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
//                Observation prediction = predictors.neural(retro);
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
