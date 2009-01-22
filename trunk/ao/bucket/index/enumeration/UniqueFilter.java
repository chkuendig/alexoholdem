package ao.bucket.index.enumeration;

import ao.bucket.index.CanonIndexed;
import ao.bucket.index.test.Gapper;
import ao.util.misc.Filter;

/**
 * Date: Jan 22, 2009
* Time: 1:51:18 PM
*/
public class UniqueFilter<T extends CanonIndexed>
        implements Filter<T>
{
    //--------------------------------------------------------------------
    private final String FORMAT;
    private final Gapper GAPPER = new Gapper();


    //--------------------------------------------------------------------
    public UniqueFilter() {  this(null);  }
    public UniqueFilter(String format) {
        FORMAT = format;
    }


    //--------------------------------------------------------------------
    public boolean accept(T indexed)
    {
        long index = indexed.packedCanonIndex();
        if (GAPPER.get( index )) return false;

        if (FORMAT != null) {
            System.out.println(String.format(FORMAT, indexed));
        }

        GAPPER.set( index );
        return true;
    }


    //--------------------------------------------------------------------
    public Gapper gapper()
    {
        return GAPPER;
    }
}
