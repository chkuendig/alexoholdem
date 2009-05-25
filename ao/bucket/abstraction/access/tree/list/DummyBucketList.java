package ao.bucket.abstraction.access.tree.list;

import ao.bucket.abstraction.access.tree.BucketList;

/**
 * Date: Jan 27, 2009
 * Time: 2:01:02 PM
 */
public class DummyBucketList implements BucketList
{
    public byte maxBuckets() {
        return 2;
    }

    public void set(long index, byte bucket)
    {

    }

    public byte get(long index)
    {
        return 0;
    }

    public void flush()
    {
    }

    public void flush(long fromCanon, char canonCount)
    {
    }
}
