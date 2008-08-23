package ao.bucket.index.iso_cards;

/**
 * Date: Aug 23, 2008
 * Time: 3:00:20 PM
 */
public interface RankedSuited<R extends Comparable<R>,
                              S extends Comparable<S>>
{
    public R rank();

    public S suit();
}
