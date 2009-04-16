package ao.simple.kuhn.player;

import ao.regret.khun.Equalibrium;
import ao.regret.khun.JointBucketSequence;
import ao.regret.khun.node.*;
import ao.simple.kuhn.KuhnAction;
import ao.simple.kuhn.KuhnCard;
import ao.simple.kuhn.KuhnPlayer;
import ao.simple.kuhn.rules.KuhnBucket;
import ao.simple.kuhn.rules.KuhnSequencer;
import ao.simple.kuhn.state.KuhnState;
import ao.util.math.stats.Combo;
import ao.util.math.stats.Permuter;
import ao.util.misc.Progress;

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

        System.out.println("Computing Equilibrium");
        Progress progres = new Progress(iterations);

        KuhnCard hands[][] = generateHands();
        for (int i = 0; i < iterations; i++)
        {
            JointBucketSequence jbs =
                    new JointBucketSequence( hands[i % hands.length] );

            equalibrium.approximate(
                    firstRoot, lastRoot,
                    jbs, 1.0, 1.0);

            progres.checkpoint();
//            System.out.println("cards: " + jbs);
//            TreeDisplay.display(firstRoot, lastRoot);
//            System.out.println("first:\n" + firstRoot);
//            System.out.println("last:\n"  + lastRoot);
        }
        TreeDisplay.display(firstRoot, lastRoot);
//        System.out.println("first:\n" + firstRoot);
//        System.out.println("last:\n"  + lastRoot);
    }


    private static KuhnCard[][] generateHands()
    {
        KuhnCard hands[][] = new KuhnCard[ (int)
                Combo.factorial(KuhnCard.values().length) ][2];

        int i = 0;
        for (KuhnCard c[] :
                new Permuter<KuhnCard>(KuhnCard.values(), 2)) {
            hands[ i++ ] = c;
        }

        return hands;
    }



    //--------------------------------------------------------------------
    public void handEnded() {}


    //--------------------------------------------------------------------
    public KuhnAction act(KuhnState state, KuhnCard hole)
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
