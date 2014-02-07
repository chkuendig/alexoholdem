package ao.holdem.ai.ai.simple.kuhn.rules;

import ao.holdem.ai.ai.simple.kuhn.KuhnCard;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public class KuhnSequencer
{
    //--------------------------------------------------------------------
    private final KuhnBucket ROOT;


    //--------------------------------------------------------------------
    public KuhnSequencer()
    {
        Collection<KuhnBucket> kids = new ArrayList<KuhnBucket>();
        for (KuhnCard c : KuhnCard.values())
        {
            kids.add( new KuhnBucket(c) );
        }
        ROOT = new KuhnBucket(null, kids);
    }


    //--------------------------------------------------------------------
    public KuhnBucket root()
    {
        return ROOT;
    }
}
