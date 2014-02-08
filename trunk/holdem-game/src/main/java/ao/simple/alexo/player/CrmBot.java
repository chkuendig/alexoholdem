package ao.simple.alexo.player;

import ao.regret.InfoNode;
import ao.regret.alexo.AlexoInfoTree;
import ao.regret.alexo.JointBucketSequence;
import ao.regret.alexo.node.BucketNode;
import ao.regret.alexo.node.OpponentNode;
import ao.regret.alexo.node.ProponentNode;
import ao.regret.alexo.pair.BucketPair;
import ao.simple.alexo.AlexoAction;
import ao.simple.alexo.AlexoPlayer;
import ao.simple.alexo.card.AlexoCardSequence;
import ao.simple.alexo.state.AlexoRound;
import ao.simple.alexo.state.AlexoState;

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
