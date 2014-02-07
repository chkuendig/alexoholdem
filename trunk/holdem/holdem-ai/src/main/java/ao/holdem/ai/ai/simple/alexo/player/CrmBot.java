package ao.holdem.ai.ai.simple.alexo.player;

import ao.holdem.ai.ai.regret.InfoNode;
import ao.holdem.ai.ai.regret.alexo.AlexoInfoTree;
import ao.holdem.ai.ai.regret.alexo.JointBucketSequence;
import ao.holdem.ai.ai.regret.alexo.node.BucketNode;
import ao.holdem.ai.ai.regret.alexo.node.OpponentNode;
import ao.holdem.ai.ai.regret.alexo.node.ProponentNode;
import ao.holdem.ai.ai.regret.alexo.pair.BucketPair;
import ao.holdem.ai.ai.simple.alexo.AlexoAction;
import ao.holdem.ai.ai.simple.alexo.AlexoPlayer;
import ao.holdem.ai.ai.simple.alexo.card.AlexoCardSequence;
import ao.holdem.ai.ai.simple.alexo.state.AlexoRound;
import ao.holdem.ai.ai.simple.alexo.state.AlexoState;

/**
 * 
 */
public class CrmBot implements AlexoPlayer
{
    //--------------------------------------------------------------------
    private final int        itr;
    private final BucketPair root;
    private       InfoNode   node = null;


    //--------------------------------------------------------------------
    public CrmBot(int iterations)
    {
        this(iterations, 0);
    }
    public CrmBot(int iterations, double aggression)
    {
        itr  = iterations;
        root = new AlexoInfoTree().root();

        for (int i = 0; i < itr; i++)
        {
            root.approximate(aggression);
        }
    }


    //--------------------------------------------------------------------
    public void handEnded(int firstToActDelta)
    {
        node = null;
    }

    public void handStarted(AlexoCardSequence cards,
                            boolean           isFirstToAct)
    {
        node = root.accordingTo(isFirstToAct);
    }

    public void roundAdvanced(AlexoRound        currentRound,
                              AlexoCardSequence cards)
    {
        JointBucketSequence buckets =
                new JointBucketSequence(cards);

        node = ((BucketNode) node)
                    .accordingTo(buckets);
    }

    public void opponentActed(AlexoState state, AlexoAction action)
    {
        node = ((OpponentNode) node).child( action );
    }


    //--------------------------------------------------------------------
    public AlexoAction act(AlexoState state, AlexoCardSequence cards)
    {
        ProponentNode myNode = (ProponentNode) node;
        
        AlexoAction act = myNode.nextAction();
        node = myNode.child( act );
        return act;
    }

    
    //--------------------------------------------------------------------
    public String toString()
    {
        return "CRM @ " + itr;
    }
}
