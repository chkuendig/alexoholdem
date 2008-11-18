package ao.bucket.abstraction.set;

/**
 * 
 */
public interface BucketSet
{
    public char bucketOf(long canonIndex);

    public void add(long canonIndex, char bucket);

    public void display();
}
