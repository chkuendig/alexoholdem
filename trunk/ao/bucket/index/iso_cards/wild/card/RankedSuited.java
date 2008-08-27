package ao.bucket.index.iso_cards.wild.card;

import ao.bucket.index.iso_cards.wild.suit.WildSuitMarker;

/**
 * Date: Aug 23, 2008
 * Time: 3:00:20 PM
 */
public interface RankedSuited<R extends Comparable<R>,
                              S extends WildSuitMarker<S>>
{
    public R rank();

    public S suit();
}
