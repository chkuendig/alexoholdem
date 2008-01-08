package ao.ai.opp_model;

import ao.ai.monte_carlo.HandApproximator;
import ao.ai.monte_carlo.PredictorService;
import ao.ai.opp_model.classifier.raw.Classifier;
import ao.ai.opp_model.classifier.raw.DomainedClassifier;
import ao.ai.opp_model.decision.classification.ConfusionMatrix;
import ao.ai.opp_model.decision.random.RandomLearner;
import ao.ai.opp_model.input.LearningPlayer;
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
//        modelOpponet(playerAccess.find("irc", "any2cnwin"));
    }


    //--------------------------------------------------------------------
    private void modelOpponet(PlayerHandle p)
    {
        System.out.println("analyzing " + p.getName());

        try
        {
            doModelAll( p );
//            doModelActions( p );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void doModelActions(PlayerHandle p)
    {
        Classifier learner =
                new DomainedClassifier( new RandomLearner.Factory() );

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
            Map<PlayerHandle, LearningPlayer> brains =
                    new HashMap<PlayerHandle, LearningPlayer>();
            for (PlayerHandle player : playerHandles)
            {
                boolean isTarget = player.equals( p );
                ModelActionPlayer actionPlayer =
                    new ModelActionPlayer(
                            isTarget,
                            hand,
                            player,
                            learner, learner);
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

    private void doModelAll(PlayerHandle p)
    {
        PredictorService predictor = new PredictorService();
        HandApproximator approx    = new HandApproximator(predictor);

        for (HandHistory hand : p.getHands())
        {
            approx.examine( hand );
            predictor.add( hand );
        }

        System.out.println( "\n\nERRORS:\n" );
        System.out.println( predictor );
    }


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
