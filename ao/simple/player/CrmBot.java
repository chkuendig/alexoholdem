package ao.simple.player;

import ao.regret.Equalibrium;
import ao.regret.JointBucketSequence;
import ao.regret.node.*;
import ao.simple.KuhnAction;
import ao.simple.KuhnCard;
import ao.simple.KuhnPlayer;
import ao.simple.rules.KuhnBucket;
import ao.simple.rules.KuhnSequencer;
import ao.simple.state.KuhnState;

/**
 * Counterfactual Regret Minimizing player
 */
public class CrmBot implements KuhnPlayer
{
    //--------------------------------------------------------------------
    private final BucketNode firstRoot;
    private final BucketNode lastRoot;


    //--------------------------------------------------------------------
    public CrmBot(int iterations)
    {
        KuhnInfoTree  tree      = new KuhnInfoTree();
        KuhnSequencer sequencer = new KuhnSequencer();

        Equalibrium equalibrium = new Equalibrium();

        firstRoot = tree.initialize(sequencer.root(), false);
        lastRoot  = tree.initialize(sequencer.root(), true);

        for (int i = 0; i < iterations; i++)
        {
            equalibrium.approximate(
                    firstRoot, lastRoot,
                    new JointBucketSequence(),
                    1.0, 1.0);
        }
    }


    //--------------------------------------------------------------------
    public void handEnded() {}


    //--------------------------------------------------------------------
    public KuhnAction act(KuhnCard hole, KuhnState state)
    {
        BucketNode node =
                state.firstToAct()
                ? firstRoot : lastRoot;

        PlayerNode firstNode = node.accordingTo( new KuhnBucket(hole) );

        switch (state)
        {
            case FIRST_ACTION: // first to act
                return ((ProponentNode) firstNode).nextAction();

            case AFTER_BET: // last to act
                ProponentNode afterBet = (ProponentNode)
                        ((OpponentNode) firstNode)
                                .child( KuhnAction.BET );
                return afterBet.nextAction();

            case AFTER_PASS:
                ProponentNode afterPass = (ProponentNode)
                        ((OpponentNode) firstNode)
                                .child( KuhnAction.PASS );
                return afterPass.nextAction();

            case AFTER_RAISE:
                OpponentNode afterFirstPass = (OpponentNode)
                        ((ProponentNode) firstNode)
                                .child( KuhnAction.PASS );

                ProponentNode afterRaise = (ProponentNode)
                        afterFirstPass.child( KuhnAction.BET );
                return afterRaise.nextAction();
        }

        return null;
    }
}
