package ao.bucket.abstraction.access.tree;

/**
 * User: alex
 * Date: 5-Jul-2009
 * Time: 5:45:05 PM
 */
public interface BucketList {
    //--------------------------------------------------------------------
    void set(long index, byte bucket);

    byte get(long index);
}
