package ao.bucket;

/**
 *
 */
public interface Bucket<B extends Bucket<B>>
{
    public double against(B otherTerminal);

    public B[] nextBuckets();
}
