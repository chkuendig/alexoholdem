package ao.simple.kuhn.player;

import ao.regret.khun.Equilibrium;
import ao.regret.khun.node.KuhnInfoTree;
import ao.simple.kuhn.KuhnAction;
import ao.simple.kuhn.KuhnCard;
import ao.simple.kuhn.KuhnPlayer;
import ao.simple.kuhn.state.KuhnState;
import ao.util.time.Progress;
import org.apache.log4j.Logger;

/**
 * User: alex
 * Date: 17-Apr-2009
 * Time: 1:09:55 PM
 */
public class Crm2Bot implements KuhnPlayer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(Crm2Bot.class);


    //--------------------------------------------------------------------
    private final KuhnInfoTree tree = new KuhnInfoTree();


    //--------------------------------------------------------------------
    public Crm2Bot(int iterations)
    {
        Equilibrium equilibrium = new Equilibrium();

        LOG.info("Computing Equilibrium");
        Progress progres = new Progress(iterations);

        KuhnCard hands[][] = KuhnCardUtils.generateHands();
        for (int i = 0; i < iterations; i++)
        {
            equilibrium.minimizeRegret(
                    tree, hands[i % hands.length]);

            progres.checkpoint();
//            System.out.println("cards: " + jbs);
//            TreeDisplay.display(firstRoot, lastRoot);
//            System.out.println("first:\n" + firstRoot);
//            System.out.println("last:\n"  + lastRoot);
        }

        LOG.info( tree );
    }


    //--------------------------------------------------------------------
    public void handEnded() {}


    //--------------------------------------------------------------------
    public KuhnAction act(KuhnState state, KuhnCard hole)
    {
        return tree.infoSet(hole, state).nextProbableAction();
    }
}
