package ao.bucket;

import ao.odds.agglom.Odds;

import java.util.Collection;

/**
 *
 */
public interface Bucket<B extends Bucket<B>>
{
    //public double transitionProbability();

    public Odds against(B otherTerminal);

    public Collection<B> nextBuckets();
}
