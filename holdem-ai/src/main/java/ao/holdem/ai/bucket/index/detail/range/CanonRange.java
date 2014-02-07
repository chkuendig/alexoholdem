package ao.holdem.ai.bucket.index.detail.range;

/**
 * Date: Jan 16, 2009
 * Time: 11:20:32 AM
 */
public class CanonRange implements Comparable<CanonRange>
{
    //--------------------------------------------------------------------
    public static CanonRange newFromTo(
            long from, long to)
    {
        return new CanonRange(from, to);
    }

    public static CanonRange newFromCount(
            long from, long count)
    {
        return new CanonRange(from, from + count - 1);
    }


    //--------------------------------------------------------------------
    private final long from;
    private final long to;


    //--------------------------------------------------------------------
    private CanonRange(long fromCanonIndexInclusive,
                       long   toCanonIndexInclusive)
    {
        from = fromCanonIndexInclusive;
        to   =   toCanonIndexInclusive;
    }


    //--------------------------------------------------------------------
    public long from() {
        return from;
    }
    public long toInclusive() {
        return to;
    }
    public long count() {
        return to - from + 1;
    }


    //--------------------------------------------------------------------
    public boolean contains(long canonIndex)
    {
        return from <= canonIndex &&
                       canonIndex <= to;
    }

    public long distanceTo(CanonRange next)
    {
        return next.from - to - 1;
    }


    //--------------------------------------------------------------------
    public int compareTo(CanonRange o) {
        return from < o.from ? -1 :
               from > o.from ?  1 : 0;
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return "CanonRange[" +
               from + ".." + to + ']';
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CanonRange that = (CanonRange) o;
        return from == that.from &&
               to   == that.to;
    }

    @Override public int hashCode()
    {
        int result = (int) (from ^ (from >>> 32));
        result = 31 * result + (int) (to ^ (to >>> 32));
        return result;
    }
}
