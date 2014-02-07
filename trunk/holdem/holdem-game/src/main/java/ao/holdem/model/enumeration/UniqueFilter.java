package ao.holdem.model.enumeration;

import ao.holdem.model.canon.CanonIndexed;
import ao.util.pass.Filter;
import org.apache.log4j.Logger;

/**
 * Date: Jan 22, 2009
* Time: 1:51:18 PM
*/
public class UniqueFilter<T extends CanonIndexed>
        implements Filter<T>
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(UniqueFilter.class);


    //--------------------------------------------------------------------
    private final String FORMAT;
    private final Gapper GAPPER;


    //--------------------------------------------------------------------
    public UniqueFilter() {  this(0, null);  }
    public UniqueFilter(String format) {
        this(0, format);
    }
    public UniqueFilter(int size) {
        this(size, null);
    }
    public UniqueFilter(int size, String format) {
        FORMAT = format;
        GAPPER = new Gapper(size);
    }



    //--------------------------------------------------------------------
    public boolean accept(T indexed)
    {
        long index = indexed.packedCanonIndex();
        if (GAPPER.get( index )) return false;

        if (FORMAT != null) {
            LOG.info(String.format(FORMAT, indexed));
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
