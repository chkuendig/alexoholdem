package ao.bucket.index.flop;

import ao.bucket.index.iso_cards.IsoFlop;
import ao.bucket.index.iso_case.FlopCase;

/**
 * Date: Aug 21, 2008
 * Time: 8:18:01 PM
 */
public interface FlopSubIndexer
{
    public int size();

    public int subIndex(IsoFlop flop);

    public boolean caseEquals(FlopCase flopCase);
}
