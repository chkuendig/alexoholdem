package ao.bucket.abstraction.access.tree;

/**
 * Date: Jan 26, 2009
 * Time: 12:25:45 PM
 */
public interface BucketList
{
    //--------------------------------------------------------------------
    public void set(long index, byte bucket);
    public byte get(long index);

    public boolean isEmpty(long index);


    //--------------------------------------------------------------------
    public void flush();
    public void flush(long fromCanon, char canonCount);
}
