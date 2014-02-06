package ao.regret.khun;

import ao.simple.kuhn.KuhnCard;
import ao.simple.kuhn.rules.KuhnBucket;

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

    public JointBucketSequence(
            KuhnCard cards[])
    {
        this(cards[0], cards[1]);
    }
    public JointBucketSequence(
            KuhnCard cardOne,
            KuhnCard cardTwo)
    {
        first = new KuhnBucket( cardOne );
        last  = new KuhnBucket( cardTwo );
    }


    //--------------------------------------------------------------------
    public KuhnBucket forPlayer(boolean firstToAct)
    {
        return firstToAct
                ? first
                : last;
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return first + "|" + last;
    }
}
