package ao.bucket.abstraction.set;

/**
 * 
 */
public interface BucketSet
{
    //--------------------------------------------------------------------
    public char bucketOf(long canonIndex);


    //--------------------------------------------------------------------
    public long[] canonsOf(char bucket);


    //--------------------------------------------------------------------
    public void add(long canonIndex, char bucket);


    //--------------------------------------------------------------------
    public void display();


    //--------------------------------------------------------------------
    public char bucketCount();


    //--------------------------------------------------------------------
    public String id();


    //--------------------------------------------------------------------
    public void persist();


    //--------------------------------------------------------------------
    public static interface Builder<T extends BucketSet>
    {
        public T newInstance(
                    long canonIndexCount,
                    char bucketCount);

        public T retrieve();
    }
}
