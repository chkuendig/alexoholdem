package ao.ai.opp_model;

import ao.ai.monte_carlo.PredictorService;
import ao.ai.opp_model.classifier.raw.Classifier;
import ao.ai.opp_model.classifier.raw.DomainedClassifier;
import ao.ai.opp_model.decision.random.RandomLearner;
import ao.ai.opp_model.decision.classification.ConfusionMatrix;
import ao.ai.opp_model.input.InputPlayer;
import ao.ai.opp_model.input.ModelActionPlayer;
import ao.holdem.engine.Dealer;
import ao.holdem.engine.LiteralCardSource;
import ao.holdem.model.act.SimpleAction;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.persist.dao.PlayerHandleAccess;
import ao.state.StateManager;
import com.google.inject.Inject;
import com.wideplay.warp.persist.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//        modelOpponet(playerAccess.find("irc", "perfecto"));
    }


    //--------------------------------------------------------------------
    private void modelOpponet(PlayerHandle p)
    {
        System.out.println("analyzing " + p.getName());

        try
        {
            //doDecisionModelOpponet( p );
            doModelActions( p );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void doModelActions(PlayerHandle p)
    {
        Classifier learner =
                new DomainedClassifier( RandomLearner.FACTORY );

        ConfusionMatrix<SimpleAction> total =
                new ConfusionMatrix<SimpleAction>();
        for (HandHistory hand : p.getHands())
        {
            //if (hand.getHoles()          == null ||
            //    hand.getHoles().get( p ) == null ||
            //    !hand.getHoles().get( p ).bothCardsVisible()) continue;
            //System.out.println(i++);
            //System.out.println(hand.summary());

            List<PlayerHandle> playerHandles =
                    new ArrayList<PlayerHandle>();
            playerHandles.addAll( hand.getPlayers() );

            StateManager start =
                    new StateManager(playerHandles,
                                     new LiteralCardSource(hand));

            ModelActionPlayer              target = null;
            Map<PlayerHandle, InputPlayer> brains =
                    new HashMap<PlayerHandle, InputPlayer>();
            for (PlayerHandle player : playerHandles)
            {
                boolean isTarget = player.equals( p );
                ModelActionPlayer actionPlayer =
                    new ModelActionPlayer(
                            hand, learner,
                            player,
                            isTarget);
                brains.put(player, actionPlayer);

                if (isTarget)
                {
                    target = actionPlayer;
                }
            }
            assert target != null;

            new Dealer(start, brains).playOutHand();
            target.addTo( total );
        }
        System.out.println(total);
    }

    private void doModelHolesIntegrated(PlayerHandle p)
    {
        PredictorService predictor = new PredictorService();

        for (HandHistory hand : p.getHands())
        {
            predictor.add( hand );
        }
    }


//    private void doDecisionModelOpponet(PlayerHandle p)
//    {
//        //Classifier learner = new GeneralTreeLearner();
//        Classifier learner = new RandomLearner();
//
//        PlayerExampleSet trainingStats   = new PlayerExampleSet();
//        PlayerExampleSet validationStats = new PlayerExampleSet();
//
//        int i = 0;
//        for (HandHistory hand : p.getHands())
//        {
//            //System.out.println(i++);
//            //System.out.println(hand.summary());
//
////            PlayerExampleSet examples =
////                    (i++ < 300) ? trainingStats
////                                : validationStats;
//            PlayerExampleSet examples = validationStats;
////            PlayerExampleSet examples = trainingStats;
//
//            List<PlayerHandle> playerHandles =
//                    new ArrayList<PlayerHandle>();
//            playerHandles.addAll( hand.getPlayers() );
//
//            StateManager start =
//                    new StateManager(playerHandles,
//                                     new LiteralCardSource(hand));
//            Map<PlayerHandle, ModelActionPlayer> brains =
//                    new HashMap<PlayerHandle, ModelActionPlayer>();
//            for (PlayerHandle player : playerHandles)
//            {
//                brains.put(player,
//                           new ModelActionPlayer(
//                                   hand, examples, player,
//                                   learner.pool(),
//                                   player.equals( p )));
//            }
//
//            new Dealer(start, brains).playOutHand();
//        }
//
//        System.out.println("building model");
//        learner.set( trainingStats.postFlops() );
//        System.out.println(learner);
////        ((RandomLearner) learner).printAsForest();
//
////        double cost         = 0;
//        int    exampleCount = 0;
//        for (Example example : validationStats.postFlops())
//        {
//            Frequency prediction =
//                    (Frequency)learner.classify( example );
//            if (prediction == null)
//                prediction = new Frequency();
//
//            MixedAction predictedAction =
//                    MixedAction.fromHistogram(prediction);
//
////            double  probability =
////                     predictedAction.probabilityOf( (SimpleAction)
////                             ((State) example.target()).state());
////
//            boolean isCorrent = example.target().equals(
//                                    prediction.mostProbable());
////            cost -= Info.log2(Math.max(0.01, probability));
////            System.out.println(
////                    ((State)example.target()).state() + "\t" +
////                    prediction  + "\t" +
////                    probability + "\t" +
////                    (isCorrent ? 1 : 0));
//            System.out.println(
//                    ((State)example.target()).state() + "\t" +
//                    predictedAction  + "\t" +
//                    (isCorrent ? 1 : 0));
//
//            trainingStats.add( (HoldemExample) example );
//            if (exampleCount++ < 50 || (exampleCount % 50 == 0))
//            {
//                learner.set( trainingStats.postFlops() );
//            }
//        }
////        System.out.println("cost = " + cost);
//    }


    //--------------------------------------------------------------------
    /*  perfecto :: 4117
        sagerbot :: 3798
        leo :: 3654
        greg :: 3140
        ab0 :: 2926
        wsk :: 2353
        Lev :: 2305
        Kai :: 2300
        derek :: 2251
        any2cnwin :: 2192 */
    private void retrieveMostPrevalent()
    {
        for (PlayerHandle p : playerAccess.byPrevalence(200))
        {
            System.out.println(
                    p.getName() + " :: " + p.getHands().size());
        }
    }
}
