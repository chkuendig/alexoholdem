package ao.regret;

import ao.simple.KuhnCard;
import ao.simple.rules.KuhnBucket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class JointBucketSequence
{
    //--------------------------------------------------------------------
    private final KuhnBucket first;
    private final KuhnBucket last;


    //--------------------------------------------------------------------
    public JointBucketSequence()
    {
        List<KuhnCard> deck =
                new ArrayList<KuhnCard>(Arrays.asList(
                        KuhnCard.values()));
        Collections.shuffle(deck);

        first = new KuhnBucket( deck.get(0) );
        last  = new KuhnBucket( deck.get(1) );
    }


    //--------------------------------------------------------------------
    public KuhnBucket forPlayer(boolean firstToAct)
    {
        return firstToAct
                ? first
                : last;
    }
}
