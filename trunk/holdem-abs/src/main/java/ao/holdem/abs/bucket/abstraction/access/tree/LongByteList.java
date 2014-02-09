package ao.holdem.abs.bucket.abstraction.access.tree;

/**
 * User: alex
 * Date: 5-Jul-2009
 * Time: 5:45:05 PM
 *
 * values are 0..255, int is used to simulate unsigned bytes
 *
 */
public interface LongByteList
{
    //--------------------------------------------------------------------
    public void set(long index, int byteValue);

    public int get(long index);


//    //--------------------------------------------------------------------
//    public long size();
}
