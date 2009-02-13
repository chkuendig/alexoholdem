package ao.bucket.index.detail;

/**
 * Date: Jan 16, 2009
 * Time: 11:20:32 AM
 */
public class CanonRange
{
    //--------------------------------------------------------------------
    private final long fromCanonIndex;
    private final long canonIndexCount;


    //--------------------------------------------------------------------
    public CanonRange(long from, long count)
    {
        fromCanonIndex  = from;
        canonIndexCount = count;
    }


    //--------------------------------------------------------------------
    public long fromCanonIndex() {
        return fromCanonIndex;
    }
    public long canonIndexCount() {
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


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return "CanonRange{" +
               "fromCanonIndex=" + fromCanonIndex +
               ", canonIndexCount=" + (int) canonIndexCount +
               '}';
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CanonRange that = (CanonRange) o;
        return canonIndexCount == that.canonIndexCount &&
               fromCanonIndex == that.fromCanonIndex;

    }

    @Override public int hashCode()
    {
        int result = (int) (fromCanonIndex ^ (fromCanonIndex >>> 32));
        result = 31 * result + (int) canonIndexCount;
        return result;
    }
}
