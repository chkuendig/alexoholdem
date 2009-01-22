package ao.bucket.index.enumeration;

import ao.bucket.index.CanonIndexed;
import ao.util.misc.Filter;

/**
 * Date: Jan 22, 2009
 * Time: 1:55:02 PM
 */
public class PermisiveFilter<T extends CanonIndexed>
        implements Filter<T>
{
    //--------------------------------------------------------------------
    private final String FORMAT;


    //--------------------------------------------------------------------
    public PermisiveFilter() {  this(null);  }
    public PermisiveFilter(String format) {
        FORMAT = format;
    }


    //--------------------------------------------------------------------
    public boolean accept(T canonIndexed)
    {
        if (FORMAT != null) {
            System.out.println(String.format(FORMAT, canonIndexed));
        }

        return true;
    }
}
