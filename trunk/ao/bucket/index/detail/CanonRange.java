package ao.bucket.index.detail;

/**
 * Date: Jan 16, 2009
 * Time: 11:20:32 AM
 */
public class CanonRange
{
    //--------------------------------------------------------------------
    private final long fromCanonIndex;
    private final char canonIndexCount;


    //--------------------------------------------------------------------
    public CanonRange(long from, char count)
    {
        fromCanonIndex  = from;
        canonIndexCount = count;
    }


    //--------------------------------------------------------------------
    public long fromCanonIndex() {
        return fromCanonIndex;
    }
    public char canonIndexCount() {
        return canonIndexCount;
    }

    public long upToAndIncluding()
    {
        return fromCanonIndex + canonIndexCount - 1;
    }


    //--------------------------------------------------------------------
    public boolean contains(long canonIndex)
    {
        return fromCanonIndex <= canonIndex &&
                                 canonIndex <= upToAndIncluding();
    }
}
