package ao.simple.kuhn.rules;

import ao.bucket.Bucket;
import ao.odds.agglom.Odds;
import ao.simple.kuhn.KuhnCard;

import java.util.Collection;

/**
 * Kuhn poker
 * Joint Bucket Sequence
 */
public class KuhnBucket implements Bucket<KuhnBucket>
{
    //--------------------------------------------------------------------
    private final KuhnCard               CARD;
    private final Collection<KuhnBucket> KIDS;


    //--------------------------------------------------------------------
    public KuhnBucket(KuhnCard card)
    {
        this(card, null);
    }

    public KuhnBucket(KuhnCard               card,
                      Collection<KuhnBucket> kids)
    {
        CARD = card;
        KIDS = kids;
    }


    //--------------------------------------------------------------------
    public double against(KuhnBucket otherTerminal)
    {
        assert CARD != null &&
               otherTerminal.CARD != null;

        return new Odds(
                CARD.compareTo(otherTerminal.CARD) > 0 ? 1 : 0,
                CARD.compareTo(otherTerminal.CARD) > 0 ? 0 : 1,
                0).strengthVsRandom();
    }

    public KuhnBucket[] nextBuckets()
    {
        return KIDS.toArray(new KuhnBucket[ KIDS.size() ]);
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return String.valueOf(CARD);
    }


    //--------------------------------------------------------------------
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KuhnBucket that = (KuhnBucket) o;
        return CARD == that.CARD &&
               !(KIDS != null
                 ? !KIDS.equals(that.KIDS)
                 : that.KIDS != null);
    }

    public int hashCode()
    {
        int result;
        result = (CARD != null ? CARD.hashCode() : 0);
        result = 31 * result +
                 (KIDS != null ? KIDS.hashCode() : 0);
        return result;
    }
}
