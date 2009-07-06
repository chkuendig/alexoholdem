package ao.bucket.abstraction.access.tree;

/**
 * Date: Jan 26, 2009
 * Time: 12:25:45 PM
 */
public interface PersistentBucketList extends BucketList
{
    //--------------------------------------------------------------------
    public byte maxBuckets();

    //--------------------------------------------------------------------
    public void flush();
}
