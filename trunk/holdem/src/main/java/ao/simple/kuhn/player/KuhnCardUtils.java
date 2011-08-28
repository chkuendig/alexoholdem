package ao.simple.kuhn.player;

import ao.simple.kuhn.KuhnCard;
import ao.util.math.stats.Combo;
import ao.util.math.stats.Permuter;

/**
 * Date: 7/9/11
 * Time: 5:08 PM
 */
public class KuhnCardUtils
{
    //------------------------------------------------------------------------
    private KuhnCardUtils() {}


    //------------------------------------------------------------------------
    public static KuhnCard[][] generateHands()
    {
        KuhnCard hands[][] = new KuhnCard[ (int)
                Combo.factorial(KuhnCard.values().length) ][2];

        int i = 0;
        for (KuhnCard[] c :
                new Permuter<KuhnCard>(KuhnCard.values(), 2)) {
            hands[ i++ ] = c;
        }

        return hands;
    }
}
