package ao.ai.monte_carlo.utc;

import ao.ai.AbstractPlayer;
import ao.holdem.engine.persist.HandHistory;
import ao.holdem.engine.state.HandState;
import ao.holdem.engine.state.StateManager;
import ao.holdem.model.act.EasyAction;
import ao.holdem.model.card.Hole;

/**
 *
 */
public class UctBot extends AbstractPlayer
{
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
//        Node root =
//                new Node(
//                        new BiState(board, you, opp, optimize),
//                        optimize);
//        for (int run = 0; run < numRuns; run++)
//        {
//            root.playSimulation();
//        }
////        System.out.println(root);
//        return root.optimize();
        return null;
    }


    //--------------------------------------------------------------------
    public void handEnded(HandHistory history)
    {

    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return super.toString() + " @ " + numRuns;
    }
}
