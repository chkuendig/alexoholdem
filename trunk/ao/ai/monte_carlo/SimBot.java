package ao.ai.monte_carlo;

import ao.ai.AbstractPlayer;
import ao.ai.opp_model.decision.classification.RealHistogram;
import ao.ai.opp_model.decision.input.raw.example.Context;
import ao.holdem.model.act.EasyAction;
import ao.holdem.model.act.SimpleAction;
import ao.holdem.model.card.Hole;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.state.HandState;
import ao.state.PlayerState;
import ao.state.StateManager;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class SimBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    private static final int SIM_COUNT = 64;


    //--------------------------------------------------------------------
//    @Inject HandApproximator handApprox;
//    @Inject PredictorService predictor;

    private HandApproximator handApprox;
    private PredictorService predictor;

    
    //--------------------------------------------------------------------
    public SimBot()
    {
        predictor  = new PredictorService();
        handApprox = new HandApproximator( predictor );
    }


    //--------------------------------------------------------------------
    protected EasyAction act(
            StateManager env,
            HandState    state,
            Hole         hole)
    {
        PlayerHandle me = env.nextToAct();

        Map<SimpleAction, int[]> counts =
                new EnumMap<SimpleAction, int[]>( SimpleAction.class );
        Map<SimpleAction, double[]> expectation =
                new EnumMap<SimpleAction, double[]>( SimpleAction.class );
        for (SimpleAction act : SimpleAction.values())
        {
            counts     .put(act, new int[1]);
            expectation.put(act, new double[1]);
        }

        for (int i = 0; i < SIM_COUNT; i++)
        {
            Map<PlayerHandle, Context> showdownContexts =
                    new HashMap<PlayerHandle, Context>();
            Map<PlayerHandle, BotPredictor> brains =
                    new HashMap<PlayerHandle, BotPredictor>();
            for (PlayerState pState : state.unfolded())
            {
                if (! pState.isAllIn())
                {
                    brains.put(pState.handle(),
                               new BotPredictor(pState.handle(),
                                                predictor));
                }
                else
                {
                    showdownContexts.put(
                            pState.handle(),
                            env.allInContext( pState.handle() ));
                }
            }

            Simulator         sim = new Simulator(env, brains);
            Simulator.Outcome out = sim.playOutHand();

            for (Map.Entry<PlayerHandle, BotPredictor> p :
                    brains.entrySet())
            {
                BotPredictor predictor = p.getValue();
                if (predictor.isUnfolded() && predictor.hasActed())
                {
                    showdownContexts.put(p.getKey(),
                                         p.getValue().lastContext());
                }
            }

            boolean iWon;
            if (showdownContexts.isEmpty())
            {
                // everybody (including me) folded leaving some player
                //  the winner without acting
                iWon = false;
            }
            else if (showdownContexts.size() == 1)
            {
                iWon = showdownContexts.keySet().iterator().next()
                        .equals( me );
            }
            else
            {
                RealHistogram<PlayerHandle> results =
                        handApprox.approximate( showdownContexts );
                iWon = results.mostProbable().equals( me );
            }

            SimpleAction act =
                    out.events().get(0).getAction().toSimpleAction();
            expectation.get(act)[0] +=
                    ((iWon ? out.totalCommit().smallBlinds() : 0)
                         - out.showdownStakes().smallBlinds())
                            * out.probability();
            counts.get(act)[0]++;
        }

        SimpleAction bestAct    = SimpleAction.FOLD;
        double       mostExpect = Long.MIN_VALUE;
        for (SimpleAction act : SimpleAction.values())
        {
            //counts.get(act)[0] "+ 1" to avoit division by zero
            double expect = expectation.get( act )[0] /
                                (counts.get(act)[0] + 1);
            if (expect > mostExpect)
            {
                mostExpect = expect;
                bestAct    = act;
            }
        }
        
        return bestAct.toEasyAction();
    }

    
    //--------------------------------------------------------------------
    public void handEnded(HandHistory history)
    {
        predictor.add( history );
    }
}
