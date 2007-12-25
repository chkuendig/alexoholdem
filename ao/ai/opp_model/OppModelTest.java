package ao.ai.opp_model;

import ao.ai.monte_carlo.PredictorService;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.persist.dao.PlayerHandleAccess;
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
//        modelOpponet(playerAccess.find("irc", "perfecto3"));
        modelOpponet(playerAccess.find("irc", "perfecto"));
    }


    //--------------------------------------------------------------------
    private void modelOpponet(PlayerHandle p)
    {
        System.out.println("analyzing " + p.getName());

        try
        {
            //doDecisionModelOpponet( p );
            doModelHoles( p );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void doModelHoles(PlayerHandle p)
    {
        PredictorService predictor = new PredictorService();

        int i = 0;
        for (HandHistory hand : p.getHands())
        {
            predictor.add( hand );

//            if (hand.getHoles()          == null ||
//                hand.getHoles().get( p ) == null ||
//                !hand.getHoles().get( p ).bothCardsVisible()) continue;
            //System.out.println(i++);
            //System.out.println(hand.summary());

//            PlayerExampleSet examples =
//                    (i++ < 300) ? trainingStats
//                                : validationStats;
//            PlayerExampleSet examples = validationStats;
//            PlayerExampleSet examples = trainingStats;

//            List<PlayerHandle> playerHandles =
//                    new ArrayList<PlayerHandle>();
//            playerHandles.addAll( hand.getPlayers() );
//
//            StateManager start =
//                    new StateManager(playerHandles,
//                                     new LiteralCardSource(hand));
//            Map<PlayerHandle, InputPlayer> brains =
//                    new HashMap<PlayerHandle, InputPlayer>();
//            for (PlayerHandle player : playerHandles)
//            {
//                brains.put(player,
//                           new ModelHolePlayer(
//                                   hand, examples, player,
//                                   learner.pool(),
//                                   player.equals( p )));
//            }
//
//            new Dealer(start, brains).playOutHand();
        }

//        System.out.println("building model");
//        learner.set( trainingStats.postFlops() );
//        System.out.println(learner);
//        ((RandomLearner) learner).printAsForest();

//        ConfusionMatrix<Datum> confusion =
//                new ConfusionMatrix<Datum>();
//
//        int    exampleCount = 0;
//        for (Example example : validationStats.postFlops())
//        {
//            Frequency prediction =
//                    (Frequency)learner.classify( example );
//            if (prediction == null)
//                prediction = new Frequency();
//
//            boolean isCorrent = example.target().equals(
//                                    prediction.mostProbable());
//            System.out.println(
//                    ((State)example.target()).state() + "\t" +
//                    prediction                        + "\t" +
//                    (isCorrent ? 1 : 0)               + "\t" +
//                    prediction.mostProbable(confusion));
//
//            confusion.add(example.target(), prediction.mostProbable());
////            confusion.add(((State)example.target()).state(),
////                          ((State)prediction.mostProbable()).state());
//            learner.add( example );
////            trainingStats.add( (HoldemExample) example );
////            if (exampleCount++ < 50 || (exampleCount % 50 == 0))
////            {
////                learner.set( trainingStats.postFlops() );
////            }
//        }
//        System.out.println("Confusion Matrix (Pivot Format): ");
//        System.out.println(confusion);
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
