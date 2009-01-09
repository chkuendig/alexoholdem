package ao.bucket;

import java.util.Collection;

/**
 *
 */
public interface Bucket<B extends Bucket<B>>
{
    public double against(B otherTerminal);

    public Collection<B> nextBuckets();
}
