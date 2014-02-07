package ao.holdem.ai.bucket.abstraction.access.tree;

/**
 * Date: Jan 26, 2009
 * Time: 12:25:45 PM
 */
public interface PersistentLongByteList extends LongByteList
{
    //--------------------------------------------------------------------
    public int maxBuckets();

    //--------------------------------------------------------------------
    public void flush();
}
