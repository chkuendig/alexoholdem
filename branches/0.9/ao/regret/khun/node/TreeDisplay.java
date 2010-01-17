package ao.regret.khun.node;

import ao.regret.InfoNode;
import ao.simple.kuhn.KuhnAction;
import ao.simple.kuhn.KuhnCard;
import ao.simple.kuhn.rules.KuhnBucket;
import ao.util.text.Txt;

/**
 * User: alex
 * Date: 15-Apr-2009
 * Time: 5:54:59 PM
 */
public class TreeDisplay
{
    //--------------------------------------------------------------------
    private TreeDisplay() {}


    //--------------------------------------------------------------------
    public static void display(
            BucketNode one,
            BucketNode two)
    {
        for (KuhnCard card : KuhnCard.values()) {
            PlayerNode a = one.accordingTo( new KuhnBucket(card) );
            PlayerNode b = two.accordingTo( new KuhnBucket(card) );

            System.out.println( card );
            display(a, b, 1);
        }
    }

    private static void display(
            PlayerNode one,
            PlayerNode two,
            int        depth)
    {
        ProponentNode proponent =
                    (one instanceof ProponentNode)
                    ? (ProponentNode) one
                    : (ProponentNode) two;

        for (KuhnAction act : KuhnAction.VALUES) {
            System.out.println(
                    Txt.nTimes("\t", depth) +
                    proponent.toString(act));

            InfoNode a = one.child(act);
            InfoNode b = two.child(act);

            if (a instanceof TerminalNode) {
                display((TerminalNode) a, (TerminalNode) b, depth + 1);
            } else {
                display((PlayerNode)   a, (PlayerNode)   b, depth + 1);
            }
        }
    }

    private static void display(
            TerminalNode one,
            TerminalNode two,
            int          depth)
    {
        System.out.println(
                Txt.nTimes("\t", depth) +
                one.toString());
    }
}
