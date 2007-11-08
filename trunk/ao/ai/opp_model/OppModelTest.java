package ao.ai.opp_model;

import ao.ai.opp_model.decision2.Classifier;
import ao.ai.opp_model.decision2.classification.Histogram;
import ao.ai.opp_model.decision2.data.State;
import ao.ai.opp_model.decision2.example.Example;
import ao.ai.opp_model.decision2.random.RandomLearner;
import ao.ai.opp_model.mix.MixedAction;
import ao.ai.opp_model.model.context.PlayerExampleSet;
import ao.ai.opp_model.model.data.ActionExample;
import ao.holdem.engine.Dealer;
import ao.holdem.engine.LiteralCardSource;
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
//        modelOpponet(playerAccess.find("irc", "perfecto3"));
        modelOpponet(playerAccess.find("irc", "perfecto"));
    }


    //--------------------------------------------------------------------
    private void modelOpponet(PlayerHandle p)
    {
        System.out.println("analyzing " + p.getName());

        try
        {
            doDecisionModelOpponet( p );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void doDecisionModelOpponet(PlayerHandle p)
    {
        //Classifier learner = new GeneralTreeLearner();
        Classifier learner = new RandomLearner();

        PlayerExampleSet trainingStats   = new PlayerExampleSet();
        PlayerExampleSet validationStats = new PlayerExampleSet();

        int i = 0;
        for (HandHistory hand : p.getHands())
        {
            //System.out.println(i++);
            //System.out.println(hand.summary());

//            PlayerExampleSet examples =
//                    (i++ < 300) ? trainingStats
//                                : validationStats;
            PlayerExampleSet examples = validationStats;
//            PlayerExampleSet examples = trainingStats;

            List<PlayerHandle> playerHandles =
                    new ArrayList<PlayerHandle>();
            playerHandles.addAll( hand.getPlayers() );

            StateManager start =
                    new StateManager(playerHandles,
                                     new LiteralCardSource(hand));
            Map<PlayerHandle, ModelPlayer> brains =
                    new HashMap<PlayerHandle, ModelPlayer>();
            for (PlayerHandle player : playerHandles)
            {
                brains.put(player,
                           new ModelPlayer(
                                   hand, examples, player,
                                   learner.pool(),
                                   player.equals( p )));
            }

            new Dealer(start, brains).playOutHand();
        }

        System.out.println("building model");
        learner.train( trainingStats.postFlops() );
        System.out.println(learner);
//        ((RandomLearner) learner).printAsForest();

//        double cost         = 0;
        int    exampleCount = 0;
        for (Example example : validationStats.postFlops())
        {
            Histogram prediction =
                    (Histogram)learner.classify( example );
            if (prediction == null)
                prediction = new Histogram();

            MixedAction predictedAction =
                    MixedAction.fromHistogram(prediction);

//            double  probability =
//                     predictedAction.probabilityOf( (SimpleAction)
//                             ((State) example.target()).state());
//
            boolean isCorrent = example.target().equals(
                                    prediction.mostProbable());
//            cost -= Info.log2(Math.max(0.01, probability));
//            System.out.println(
//                    ((State)example.target()).state() + "\t" +
//                    prediction  + "\t" +
//                    probability + "\t" +
//                    (isCorrent ? 1 : 0));
            System.out.println(
                    ((State)example.target()).state() + "\t" +
                    predictedAction  + "\t" +
                    (isCorrent ? 1 : 0));

            trainingStats.add( (ActionExample) example );
            if (exampleCount++ < 50 || (exampleCount % 50 == 0))
            {
                learner.train( trainingStats.postFlops() );
            }
        }
//        System.out.println("cost = " + cost);
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
