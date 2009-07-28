package ao.bucket.abstraction.access.tree;

/**
 * User: alex
 * Date: 5-Jul-2009
 * Time: 5:45:05 PM
 */
public interface LongByteList
{
    //--------------------------------------------------------------------
    public void set(long index, byte bucket);

    public byte get(long index);


//    //--------------------------------------------------------------------
//    public long size();
}
