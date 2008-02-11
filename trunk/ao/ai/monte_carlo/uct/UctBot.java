package ao.ai.monte_carlo.uct;

import ao.ai.AbstractPlayer;
import ao.ai.opp_model.predict.PredictorService;
import ao.holdem.engine.persist.HandHistory;
import ao.holdem.engine.state.HandState;
import ao.holdem.engine.state.StateManager;
import ao.holdem.model.act.EasyAction;
import ao.holdem.model.card.Hole;
import com.google.inject.Inject;

/**
 *
 */
public class UctBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    @Inject PredictorService predictor;


    //--------------------------------------------------------------------
    private int numRuns;


    //--------------------------------------------------------------------
    public UctBot(int iterations)
    {
        numRuns = iterations;
    }
    public UctBot()
    {
        this(64);
    }


    //--------------------------------------------------------------------
    public void startThinking() {}
    public void stopThinking()  {}


    //--------------------------------------------------------------------
    protected EasyAction act(StateManager env,
                             HandState    state,
                             Hole         hole)
    {
        Node root = new Node(env);
        for (int run = 0; run < numRuns; run++)
        {
            root.playSimulation(predictor);
        }
////        System.out.println(root);
        return root.optimize().toEasyAction();
    }


    //--------------------------------------------------------------------
    public void handEnded(HandHistory history)
    {
        toString();
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return super.toString() + " @ " + numRuns;
    }
}
