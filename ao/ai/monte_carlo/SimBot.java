package ao.ai.monte_carlo;

import ao.ai.AbstractPlayer;
import ao.ai.opp_model.decision.classification.RealHistogram;
import ao.holdem.model.act.EasyAction;
import ao.holdem.model.act.SimpleAction;
import ao.holdem.model.card.Hole;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.state.HandState;
import ao.state.PlayerState;
import ao.state.StateManager;
import com.google.inject.Inject;

import java.util.*;

/**
 *
 */
public class SimBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    private static final int SIM_COUNT = 64;


    //--------------------------------------------------------------------
    @Inject PredictorService predictor;
//    private PredictorService predictor;

    
    //--------------------------------------------------------------------
    public SimBot()
    {
        //predictor = new PredictorService();
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
            counts     .put(act, new int   [1]);
            expectation.put(act, new double[1]);
        }

        Map<PlayerHandle, List<Choice>> baseChoices =
                predictor.extractChoices( env.toHistory() );

        for (int i = 0; i < SIM_COUNT; i++)
        {
            List<PlayerHandle> atShowdown = new ArrayList<PlayerHandle>();
            Map<PlayerHandle, List<Choice>> choices =
                    new HashMap<PlayerHandle, List<Choice>>(baseChoices);
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
                    atShowdown.add( pState.handle() );
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
                    List<Choice> base = choices.get( p.getKey() );
                    if (base == null)
                    {
                        base = new ArrayList<Choice>();
                    }
                    base.addAll( p.getValue().choices() );
                    atShowdown.add( p.getKey() );
                }
            }
            choices.keySet().retainAll( atShowdown );

            double iWon;
            if (choices.isEmpty())
            {
                // everybody (including me) folded, leaving some player
                //  the winner without acting
                iWon = 0.0;
            }
            else if (choices.size() == 1)
            {
                iWon = choices.keySet().iterator().next().equals( me )
                       ? 1.0 : 0.0;
            }
            else
            {
                RealHistogram<PlayerHandle> results =
                        predictor.approximate( choices );
                iWon = results.probabilityOf( me );
            }

            int ifLoss =
                    choices.containsKey( me )
                    ? -out.showdownStakes().smallBlinds()
                    : -brains.get( me ).lastChoice().state()
                            .stakes().smallBlinds();
            int ifWin  = out.totalCommit().smallBlinds();

            SimpleAction act =
                    out.events().get(0).getAction().toSimpleAction();
            expectation.get(act)[0] +=
                    (iWon * ifWin + (1.0 - iWon) * ifLoss)
                      * out.probability();
            counts.get(act)[0]++;
        }

        SimpleAction bestAct    = SimpleAction.FOLD;
        double       mostExpect = Long.MIN_VALUE;
        for (SimpleAction act : SimpleAction.values())
        {
            if (counts.get(act)[0] == 0) continue;

            double expect = expectation.get( act )[0] /
                                counts.get( act )[0];
            //System.out.println(act + "\t" + expect);

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
