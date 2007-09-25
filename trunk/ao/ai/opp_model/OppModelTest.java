package ao.ai.opp_model;

import ao.ai.opp_model.decision.DecisionLearner;
import ao.ai.opp_model.decision.data.DataSet;
import ao.ai.opp_model.decision.data.Example;
import ao.ai.opp_model.decision.data.Histogram;
import ao.ai.opp_model.decision.domain.HoldemHandParser;
import ao.ai.opp_model.decision.tree.DecisionTreeLearner;
import ao.holdem.engine.Dealer;
import ao.holdem.model.act.SimpleAction;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.persist.dao.PlayerHandleAccess;
import ao.state.StateManager;
import ao.stats.HandStats;
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
        retrieveMostPrevalent();
//        modelOpponet(playerAccess.find("irc", "sagerbot"));
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
        HoldemHandParser decisionSetup   = new HoldemHandParser();
        HandStats        trainingStats   = new HandStats();
        HandStats        validationStats = new HandStats();

        int i = 0;
        for (HandHistory hand : p.getHands())
        {
            System.out.println(i);

            HandStats stats = (i++ < 300)
                              ? trainingStats
                              : validationStats;

            List<PlayerHandle> playerHandles =
                    new ArrayList<PlayerHandle>();
            playerHandles.addAll( hand.getPlayers() );

            StateManager start = new StateManager(playerHandles);
            Map<PlayerHandle, ModelPlayer> brains =
                    new HashMap<PlayerHandle, ModelPlayer>();
            for (PlayerHandle player : playerHandles)
            {
                brains.put(player, new ModelPlayer(hand, stats));
            }

            new Dealer(start, brains).playOutHand();
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
