package ao.bucket;

import java.util.Collection;

/**
 *
 */
public interface Bucket
{
    public double transitionProbability();

    public Collection<Bucket> nextBuckets();
}
