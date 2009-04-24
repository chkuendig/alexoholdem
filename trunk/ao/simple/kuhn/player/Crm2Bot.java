package ao.simple.kuhn.player;

import ao.regret.khun.Equalibrium;
import ao.regret.khun.node.KuhnInfoTree;
import ao.simple.kuhn.KuhnAction;
import ao.simple.kuhn.KuhnCard;
import ao.simple.kuhn.KuhnPlayer;
import ao.simple.kuhn.state.KuhnState;
import ao.util.math.stats.Combo;
import ao.util.math.stats.Permuter;
import ao.util.time.Progress;

/**
 * User: alex
 * Date: 17-Apr-2009
 * Time: 1:09:55 PM
 */
public class Crm2Bot implements KuhnPlayer
{
    //--------------------------------------------------------------------
    private final KuhnInfoTree tree = new KuhnInfoTree();


    //--------------------------------------------------------------------
    public Crm2Bot(int iterations)
    {
        Equalibrium equalibrium = new Equalibrium();

        System.out.println("Computing Equilibrium");
        Progress progres = new Progress(iterations);

        KuhnCard hands[][] = generateHands();
        for (int i = 0; i < iterations; i++)
        {
            equalibrium.minimizeRegret(
                    tree, hands[i % hands.length]);

            progres.checkpoint();
//            System.out.println("cards: " + jbs);
//            TreeDisplay.display(firstRoot, lastRoot);
//            System.out.println("first:\n" + firstRoot);
//            System.out.println("last:\n"  + lastRoot);
        }

        System.out.println( tree );
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
        return tree.infoSet(hole, state).nextProbableAction();
    }
}
